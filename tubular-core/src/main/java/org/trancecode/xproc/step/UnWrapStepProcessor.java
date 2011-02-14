/*
 * Copyright (C) 2011 Emmanuel Tourdot
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */
package org.trancecode.xproc.step;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import net.sf.saxon.s9api.*;
import org.apache.commons.lang.StringUtils;
import org.trancecode.xml.saxon.*;
import org.trancecode.xproc.XProcException;
import org.trancecode.xproc.XProcExceptions;
import org.trancecode.xproc.port.XProcPorts;
import org.trancecode.xproc.variable.XProcOptions;

import java.util.EnumSet;

/**
 * {@code p:unwrap}.
 * 
 * @author Emmanuel Tourdot
 * @see <a
 *      href="http://www.w3.org/TR/xproc/#c.unwrap">p:unwrap</a>
 */
public final class UnWrapStepProcessor extends AbstractStepProcessor
{
    @Override
    public QName getStepType()
    {
        return XProcSteps.UNWRAP;
    }

    @Override
    protected void execute(final StepInput input, final StepOutput output)
    {
        final XdmNode sourceDocument = input.readNode(XProcPorts.SOURCE);
        final String match = input.getOptionValue(XProcOptions.MATCH);

        final SaxonProcessorDelegate unWrapDelegate = new AbstractSaxonProcessorDelegate()
        {
            @Override
            public boolean startDocument(XdmNode node, SaxonBuilder builder)
            {
                return true;
            }

            @Override
            public EnumSet<NextSteps> startElement(XdmNode node, SaxonBuilder builder)
            {
                return EnumSet.of(NextSteps.PROCESS_ATTRIBUTES, NextSteps.PROCESS_CHILDREN, NextSteps.START_CONTENT);
            }

            @Override
            public void endDocument(XdmNode node, SaxonBuilder builder)
            {
            }

            @Override
            public void endElement(XdmNode node, SaxonBuilder builder)
            {
            }
        };

        final SaxonProcessorDelegate unWrapWithError = SaxonProcessorDelegates.forNodeKinds(ImmutableSet.of(XdmNodeKind.ELEMENT), unWrapDelegate,
                SaxonProcessorDelegates.error(new Function<XdmNode, XProcException>()
                {
                    @Override
                    public XProcException apply(final XdmNode node)
                    {
                        return XProcExceptions.xc0023(node, XdmNodeKind.ELEMENT);
                    }
                }));

        final SaxonProcessor unWrapProcessor = new SaxonProcessor(input.getPipelineContext().getProcessor(),
                SaxonProcessorDelegates.forXsltMatchPattern(input.getPipelineContext().getProcessor(), match, input
                        .getStep().getNode(), unWrapWithError, new CopyingSaxonProcessorDelegate()));

        final XdmNode result = unWrapProcessor.apply(sourceDocument);

        output.writeNodes(XProcPorts.RESULT, result);
    }

    private boolean nextInGroupAdjacent(final String groupAdjacent, final XdmNode node)
    {
        return inGroupAdjacent(groupAdjacent, node, true);
    }

    private boolean prevInGroupAdjacent(final String groupAdjacent, final XdmNode node)
    {
        return inGroupAdjacent(groupAdjacent, node, false);
    }

    private boolean inGroupAdjacent(final String groupAdjacent, final XdmNode node, final boolean next)
    {
        if (groupAdjacent == null)
        {
            return false;
        }
        try
        {
            final XPathCompiler xPathCompiler = node.getProcessor().newXPathCompiler();
            final XPathSelector xPathSelector = xPathCompiler.compile(groupAdjacent).load();
            xPathSelector.setContextItem(node);
            final XdmItem item1 = xPathSelector.evaluateSingle();
            final XdmSequenceIterator iterator = (next)? node.axisIterator(Axis.FOLLOWING_SIBLING) :
                        node.axisIterator(Axis.PRECEDING_SIBLING);
            final XdmItem item2 = getNextSkpNode(iterator, xPathSelector);
             if (item2 != null)
            {
                return item1.getStringValue().equals(item2.getStringValue());
            }
            else
            {
                return false;
            }
        }
        catch (SaxonApiException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private XdmItem getNextSkpNode(final XdmSequenceIterator iterator, final XPathSelector xPathSelector)
    {
        while (iterator.hasNext())
        {
            final XdmNode itm = (XdmNode) iterator.next();
            if (XdmNodeKind.ELEMENT.equals(itm.getNodeKind()) ||
                (XdmNodeKind.TEXT.equals(itm.getNodeKind()) && !"".equals(StringUtils.trim(itm.getStringValue()))))
            {
                try
                {
                    xPathSelector.setContextItem(itm);
                    return xPathSelector.evaluateSingle();
                }
                catch (SaxonApiException e)
                {
                    return null;
                }
            }
        }
        return null;
    }
}
