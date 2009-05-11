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
package org.trancecode.core;

import org.trancecode.annotation.ReturnsNullable;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;


/**
 * @author Herve Quiroz
 * @version $Revision$
 */
public final class CollectionUtil
{
	private CollectionUtil()
	{
		// No instantiation
	}


	public static <K, V> Map<K, V> newSmallWriteOnceMap()
	{
		// TODO
		return Maps.newLinkedHashMap();
	}


	public static <K, V> Map<K, V> merge(final Map<K, V> map1, final Map<K, V> map2)
	{
		final Map<K, V> map = Maps.newHashMapWithExpectedSize(map1.size() + map2.size());
		map.putAll(map1);
		map.putAll(map2);

		return map;
	}


	public static <K, V> Map<K, V> copyAndPut(final Map<K, V> map1, final K key, final V value)
	{
		final Map<K, V> map = Maps.newHashMap(map1);
		map.put(key, value);

		return map;
	}


	public static <E, P> E apply(
		final E initialElement, final Iterable<P> parameters, final BinaryFunction<E, E, P> function)
	{
		E currentElement = initialElement;
		for (final P parameter : parameters)
		{
			currentElement = function.evaluate(currentElement, parameter);
		}

		return currentElement;
	}


	@ReturnsNullable
	public static <T> T getLast(final Iterable<T> elements)
	{
		if (elements instanceof List)
		{
			return getLast(elements);
		}

		try
		{
			return Iterators.getLast(elements.iterator());
		}
		catch (final NoSuchElementException e)
		{
			return null;
		}
	}


	@ReturnsNullable
	public static <T> T getLast(final List<T> elements)
	{
		if (elements.isEmpty())
		{
			return null;
		}

		return elements.get(elements.size() - 1);
	}


	public static <T> Iterable<T> append(final Iterable<T> iterable, final T... elements)
	{
		return Iterables.concat(iterable, ImmutableList.of(elements));
	}
}
