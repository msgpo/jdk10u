/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file, and Oracle licenses the original version of this file under the BSD
 * license:
 */
/*
   Copyright 2015 Attila Szegedi

   Licensed under both the Apache License, Version 2.0 (the "Apache License")
   and the BSD License (the "BSD License"), with licensee being free to
   choose either of the two at their discretion.

   You may not use this file except in compliance with either the Apache
   License or the BSD License.

   If you choose to use this file in compliance with the Apache License, the
   following notice applies to you:

       You may obtain a copy of the Apache License at

           http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
       implied. See the License for the specific language governing
       permissions and limitations under the License.

   If you choose to use this file in compliance with the BSD License, the
   following notice applies to you:

       Redistribution and use in source and binary forms, with or without
       modification, are permitted provided that the following conditions are
       met:
       * Redistributions of source code must retain the above copyright
         notice, this list of conditions and the following disclaimer.
       * Redistributions in binary form must reproduce the above copyright
         notice, this list of conditions and the following disclaimer in the
         documentation and/or other materials provided with the distribution.
       * Neither the name of the copyright holder nor the names of
         contributors may be used to endorse or promote products derived from
         this software without specific prior written permission.

       THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
       IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
       TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
       PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL COPYRIGHT HOLDER
       BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
       CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
       SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
       BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
       WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
       OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
       ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package jdk.internal.dynalink;

import java.util.Objects;

/**
 * Operation that associates a name with another operation. Typically used with
 * operations that normally take a name or an index to bind them to a fixed
 * name. E.g. {@code new NamedOperation(StandardOperation.GET_PROPERTY, "color")}
 * will be a named operation for getting the property named "color" on the
 * object it is applied to, and
 * {@code new NamedOperation(StandardOperation.GET_ELEMENT, 3)} will be a named
 * operation for getting the element at index 3 from the collection it is
 * applied to. In these cases, the expected signature of the call site for the
 * operation will change to no longer include the name parameter. Specifically,
 * the documentation for all {@link StandardOperation} members describes how
 * they are affected by being incorporated into a named operation.
 */
public class NamedOperation implements Operation {
    private final Operation baseOperation;
    private final Object name;

    /**
     * Creates a new named operation.
     * @param baseOperation the base operation that is associated with a name.
     * @param name the name associated with the base operation. Note that the
     * name is not necessarily a string, but can be an arbitrary object. As the
     * name is used for addressing, it can be an {@link Integer} when meant
     * to be used as an index into an array or list etc.
     * @throws NullPointerException if either {@code baseOperation} or
     * {@code name} is null.
     * @throws IllegalArgumentException if {@code baseOperation} is itself a
     * {@code NamedOperation}.
     */
    public NamedOperation(final Operation baseOperation, final Object name) {
        if (baseOperation instanceof NamedOperation) {
            throw new IllegalArgumentException("baseOperation is a named operation");
        }
        this.baseOperation = Objects.requireNonNull(baseOperation, "baseOperation is null");
        this.name = Objects.requireNonNull(name, "name is null");
    }

    /**
     * Returns the base operation of this named operation.
     * @return the base operation of this named operation.
     */
    public Operation getBaseOperation() {
        return baseOperation;
    }

    /**
     * Returns the name of this named operation.
     * @return the name of this named operation.
     */
    public Object getName() {
        return name;
    }

    /**
     * Compares this named operation to another object. Returns true if the
     * other object is also a named operation, and both their base operations
     * and name are equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        } else if(obj.getClass() != NamedOperation.class) {
            return false;
        }
        final NamedOperation other = (NamedOperation)obj;
        return baseOperation.equals(other.baseOperation) && name.equals(other.name);
    }

    /**
     * Returns the hash code of this named operation. It is defined to be equal
     * to {@code baseOperation.hashCode() + 31 * name.hashCode()}.
     */
    @Override
    public int hashCode() {
        return baseOperation.hashCode() + 31 * name.hashCode();
    }

    /**
     * Returns the string representation of this named operation. It is defined
     * to be equal to {@code baseOperation.toString() + ":" + name.toString()}.
     */
    @Override
    public String toString() {
        return baseOperation.toString() + ":" + name.toString();
    }

    /**
     * If the passed operation is a named operation, returns its
     * {@link #getBaseOperation()}, otherwise returns the operation as is.
     * @param op the operation
     * @return the base operation of the passed operation.
     */
    public static Operation getBaseOperation(final Operation op) {
        return op instanceof NamedOperation ? ((NamedOperation)op).baseOperation : op;
    }

    /**
     * If the passed operation is a named operation, returns its
     * {@link #getName()}, otherwise returns null. Note that a named operation
     * object can never have a null name, therefore returning null is indicative
     * that the passed operation is not, in fact, a named operation.
     * @param op the operation
     * @return the name in the passed operation, or null if it is not a named
     * operation.
     */
    public static Object getName(final Operation op) {
        return op instanceof NamedOperation ? ((NamedOperation)op).name : null;
    }
}