/**
 * Copyright (c) 2008-2011, Open & Green Inc.
 * All rights reserved.
 * info@gaoshin.com
 * 
 * This version of SORMA is licensed under the terms of the Open Source GPL 3.0 license. http://www.gnu.org/licenses/gpl.html. Alternate Licenses are available.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, 
 * AND NON-INFRINGEMENT OF THIRD-PARTY INTELLECTUAL PROPERTY RIGHTS.  
 * See the GNU General Public License for more details.
 * 
 */
package com.gaoshin.sorma.annotation;

@SuppressWarnings("serial")
public class SormaException extends RuntimeException {
    public SormaException() {
    }

    public SormaException(String msg) {
    	super(msg);
    }

    public SormaException(Throwable t) {
        super(t);
    }

    public SormaException(String msg, Throwable t) {
        super(msg, t);
    }
}
