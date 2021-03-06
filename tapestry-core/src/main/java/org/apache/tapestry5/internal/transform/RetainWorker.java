// Copyright 2006, 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.transform;

import org.apache.tapestry5.annotations.Retain;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticField;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;

/**
 * Identifies fields with the {@link org.apache.tapestry5.annotations.Retain} annotation, and "claims" them so that no
 * special work will occur on them. Retain has been deprecated in Tapestry 5.2 and will likely be removed in the future.
 */
public final class RetainWorker implements ComponentClassTransformWorker2
{
    /**
     * Claims each field with the {@link org.apache.tapestry5.annotations.Retain}  , claiming it using the
     * annotation class (not the annotation instance, to avoid
     * instantiating the annotation) as the tag.
     */
    public void transform(PlasticClass plasticClass, TransformationSupport support, MutableComponentModel model)
    {
        for (PlasticField field : plasticClass.getFieldsWithAnnotation(Retain.class))
        {
            field.claim(Retain.class);
        }
    }
}
