/*
 * Copyright (C) 2008 TranceCode Software
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
 *
 * $Id$
 */
package org.trancecode.xproc;

import org.trancecode.annotation.Nullable;

import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;


/**
 * @author Herve Quiroz
 * @version $Revision$
 */
public final class XProcUtil
{
	private XProcUtil()
	{
		// No instantiation
	}


	public static XdmNode newParameterElement(final QName name, final String value, final Processor processor)
	{
		// TODO use s9api directly
		final String document =
			String.format(
				"<c:param xmlns:c=\"%s\" name=\"%s\" value=\"%s\"/>", XProcNamespaces.URI_XPROC_STEP, name, value);
		try
		{
			return processor.newDocumentBuilder().build(new StreamSource(new StringReader(document)));
		}
		catch (final SaxonApiException e)
		{
			throw new IllegalStateException(e);
		}
	}


	public static XdmNode newResultElement(final String value, final Processor processor)
	{
		// TODO use s9api directly
		final String document =
			String.format("<c:result xmlns:c=\"%s\">%s</c:result>", XProcNamespaces.URI_XPROC_STEP, value);
		try
		{
			return processor.newDocumentBuilder().build(new StreamSource(new StringReader(document)));
		}
		catch (final SaxonApiException e)
		{
			throw new IllegalStateException(e);
		}
	}


	public static boolean isPipeline(@Nullable final Step step)
	{
		return step != null && step.isCompoundStep() && !XProcSteps.ALL_STEPS.contains(step.getType())
			&& !XProcSteps.PIPELINE.equals(step.getType());
	}
}
