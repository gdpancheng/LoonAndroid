/*
 * Copyright (C) 2012 Mobs and Geeks
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file 
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.android.pc.ioc.verification.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.android.pc.ioc.verification.Rules;

/**
 * Number rule annotation. Allows a specific primitive type contained in {@link NumberType}.
 * Additional options such as greater than (>), less than (<) and equals (==) are available. 
 *
 * @author Ragunath Jawahar <rj@mobsandgeeks.com>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberRule {
    public int order();
    /**
     * 类型
     * @author gdpancheng@gmail.com 2014-1-22 下午11:03:22
     * @return
     * @return NumberType
     */
    public NumberType type();
    /**
     * 最小
     * @author gdpancheng@gmail.com 2014-1-22 下午10:58:44
     * @return
     * @return double
     */
    public double gt()          default Double.MAX_VALUE;
    /**
     * 最大
     * @author gdpancheng@gmail.com 2014-1-22 下午10:59:02
     * @return
     * @return double
     */
    public double lt()          default Double.MIN_VALUE;
    /**
     * 等于
     * @author gdpancheng@gmail.com 2014-1-22 下午11:03:43
     * @return
     * @return double
     */
    public double eq()          default Double.MAX_VALUE;
    public String message()     default Rules.EMPTY_STRING;
    public int messageResId()   default 0;

    public enum NumberType {
        INTEGER, LONG, FLOAT, DOUBLE
    }
}
