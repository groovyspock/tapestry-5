// Copyright 2007, 2008, 2010 The Apache Software Foundation
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

package org.apache.tapestry5.internal.hibernate;

import org.apache.tapestry5.ioc.test.TestBase;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.Test;

@Test
public class DefaultHibernateConfigurerFilterTest extends TestBase
{
    public void configure_with_default_configuration_on() throws Exception
    {
        Configuration config = newMock(Configuration.class);
        expect(config.configure()).andReturn(config);

        replay();

        new DefaultHibernateConfigurer(true).configure(config);

        verify();
    }

    public void configure_with_default_configuration_off() throws Exception
    {
        Configuration config = newMock(Configuration.class);

        replay();

        new DefaultHibernateConfigurer(false).configure(config);

        verify();
    }
}
