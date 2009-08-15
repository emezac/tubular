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
package org.trancecode.xml.catalog;

import org.trancecode.annotation.Nullable;
import org.trancecode.annotation.ReturnsNullable;
import org.trancecode.core.TubularObjects;
import org.trancecode.io.Uris;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Herve Quiroz
 * @version $Revision$
 */
public class RewriteUri extends AbstractCatalog
{
	private static final Logger logger = LoggerFactory.getLogger(RewriteUri.class);

	private final String uriStartString;
	private final String rewritePrefix;


	public RewriteUri(final String uriStartString, final String rewritePrefix, final URI baseUri)
	{
		super(baseUri);

		assert uriStartString != null;
		this.uriStartString = uriStartString;

		assert rewritePrefix != null;
		this.rewritePrefix = rewritePrefix;
	}


	@Override
	@ReturnsNullable
	protected URI doResolve(
		@Nullable final String publicId, @Nullable final String systemId, @Nullable final String href,
		@Nullable final String base)
	{
		logger.trace("publicId = {} ; systemId = {} ; href = {} ; base = {}", new Object[] { publicId, systemId, href,
			base });

		final String uriString;
		if (href != null || base != null)
		{
			uriString = Uris.resolve(href, base).toString();
		}
		else
		{
			uriString = systemId;
		}

		logger.trace("uriString = {}", uriString);

		if (uriString != null && uriString.startsWith(uriStartString))
		{
			final String suffix = uriString.substring(uriStartString.length());
			return Uris.createUri(rewritePrefix + suffix);
		}

		return null;
	}


	@Override
	public int hashCode()
	{
		return TubularObjects.hashCode(RewriteUri.class, baseUri, uriStartString, rewritePrefix);
	}


	@Override
	public boolean equals(final Object o)
	{
		if (o != null && o instanceof RewriteUri)
		{
			final RewriteUri rewriteUri = (RewriteUri)o;
			return TubularObjects.pairEquals(
				baseUri, rewriteUri.baseUri, uriStartString, rewriteUri.uriStartString, rewritePrefix,
				rewriteUri.rewritePrefix);
		}

		return false;
	}
}
