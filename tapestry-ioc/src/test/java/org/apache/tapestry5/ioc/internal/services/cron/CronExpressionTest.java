// Copyright 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.ioc.internal.services.cron;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class CronExpressionTest extends Assert
{

    /*
    * Test method for 'org.quartz.CronExpression.isSatisfiedBy(Date)'.
    */
    @Test
    public void testIsSatisfiedBy() throws Exception
    {
        CronExpression cronExpression = new CronExpression("0 15 10 * * ? 2005");

        Calendar cal = Calendar.getInstance();

        cal.set(2005, Calendar.JUNE, 1, 10, 15, 0);
        assertTrue(cronExpression.isSatisfiedBy(cal.getTime()));

        cal.set(Calendar.YEAR, 2006);
        assertFalse(cronExpression.isSatisfiedBy(cal.getTime()));

        cal = Calendar.getInstance();
        cal.set(2005, Calendar.JUNE, 1, 10, 16, 0);
        assertFalse(cronExpression.isSatisfiedBy(cal.getTime()));

        cal = Calendar.getInstance();
        cal.set(2005, Calendar.JUNE, 1, 10, 14, 0);
        assertFalse(cronExpression.isSatisfiedBy(cal.getTime()));
    }

    @Test
    public void testLastDayOffset() throws Exception
    {
        CronExpression cronExpression = new CronExpression("0 15 10 L-2 * ? 2010");

        Calendar cal = Calendar.getInstance();

        cal.set(2010, Calendar.OCTOBER, 29, 10, 15, 0); // last day - 2
        assertTrue(cronExpression.isSatisfiedBy(cal.getTime()));

        cal.set(2010, Calendar.OCTOBER, 28, 10, 15, 0);
        assertFalse(cronExpression.isSatisfiedBy(cal.getTime()));

        cronExpression = new CronExpression("0 15 10 L-5W * ? 2010");

        cal.set(2010, Calendar.OCTOBER, 26, 10, 15, 0); // last day - 5
        assertTrue(cronExpression.isSatisfiedBy(cal.getTime()));

        cronExpression = new CronExpression("0 15 10 L-1 * ? 2010");

        cal.set(2010, Calendar.OCTOBER, 30, 10, 15, 0); // last day - 1
        assertTrue(cronExpression.isSatisfiedBy(cal.getTime()));

        cronExpression = new CronExpression("0 15 10 L-1W * ? 2010");

        cal.set(2010, Calendar.OCTOBER, 29, 10, 15, 0); // nearest weekday to last day - 1 (29th is a friday in 2010)
        assertTrue(cronExpression.isSatisfiedBy(cal.getTime()));

    }

    /*
     * QUARTZ-571: Showing that expressions with months correctly serialize.
     * Note: currently disabled; seems to be broken when building with JDK 1.7.
     */
    @Test(enabled = false)
    public void testQuartz571() throws Exception
    {
        CronExpression cronExpression = new CronExpression("19 15 10 4 Apr ? ");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(cronExpression);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        CronExpression newExpression = (CronExpression) ois.readObject();

        assertEquals(newExpression.getCronExpression(), cronExpression.getCronExpression());

        // if broken, this will throw an exception
        newExpression.getNextValidTimeAfter(new Date());
    }

    /*
     * QUARTZ-574: Showing that storeExpressionVals correctly calculates the month number
     */
    @Test
    public void testQuartz574()
    {
        try
        {
            new CronExpression("* * * * Foo ? ");
            fail("Expected ParseException did not fire for non-existent month");
        } catch (ParseException pe)
        {
            assertTrue(pe.getMessage().startsWith("Invalid Month value:"), "Incorrect ParseException thrown");
        }

        try
        {
            new CronExpression("* * * * Jan-Foo ? ");
            fail("Expected ParseException did not fire for non-existent month");
        } catch (ParseException pe)
        {
            assertTrue(pe.getMessage().startsWith("Invalid Month value:"), "Incorrect ParseException thrown");
        }
    }

    @Test
    public void testQuartz621()
    {
        try
        {
            new CronExpression("0 0 * * * *");
            fail("Expected ParseException did not fire for wildcard day-of-month and day-of-week");
        } catch (ParseException pe)
        {
            assertTrue(
                    pe.getMessage().startsWith("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented."),
                    "Incorrect ParseException thrown");
        }
        try
        {
            new CronExpression("0 0 * 4 * *");
            fail("Expected ParseException did not fire for specified day-of-month and wildcard day-of-week");
        } catch (ParseException pe)
        {
            assertTrue(
                    pe.getMessage().startsWith("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented."),
                    "Incorrect ParseException thrown");
        }
        try
        {
            new CronExpression("0 0 * * * 4");
            fail("Expected ParseException did not fire for wildcard day-of-month and specified day-of-week");
        } catch (ParseException pe)
        {
            assertTrue(
                    pe.getMessage().startsWith("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented."),
                    "Incorrect ParseException thrown");
        }
    }

    @Test
    public void testQuartz640() throws ParseException
    {
        try
        {
            new CronExpression("0 43 9 1,5,29,L * ?");
            fail("Expected ParseException did not fire for L combined with other days of the month");
        } catch (ParseException pe)
        {
            assertTrue(
                    pe.getMessage().startsWith("Support for specifying 'L' and 'LW' with other days of the month is not implemented"),
                    "Incorrect ParseException thrown");
        }
        try
        {
            new CronExpression("0 43 9 ? * SAT,SUN,L");
            fail("Expected ParseException did not fire for L combined with other days of the week");
        } catch (ParseException pe)
        {
            assertTrue(
                    pe.getMessage().startsWith("Support for specifying 'L' with other days of the week is not implemented"),
                    "Incorrect ParseException thrown");
        }
        try
        {
            new CronExpression("0 43 9 ? * 6,7,L");
            fail("Expected ParseException did not fire for L combined with other days of the week");
        } catch (ParseException pe)
        {
            assertTrue(
                    pe.getMessage().startsWith("Support for specifying 'L' with other days of the week is not implemented"),
                    "Incorrect ParseException thrown");
        }
        try
        {
            new CronExpression("0 43 9 ? * 5L");
        } catch (ParseException pe)
        {
            fail("Unexpected ParseException thrown for supported '5L' expression.");
        }
    }


    @Test
    public void testQtz96() throws ParseException
    {
        try
        {
            new CronExpression("0/5 * * 32W 1 ?");
            fail("Expected ParseException did not fire for W with value larger than 31");
        } catch (ParseException pe)
        {
            assertTrue(
                    pe.getMessage().startsWith("The 'W' option does not make sense with values larger than"),
                    "Incorrect ParseException thrown");
        }
    }
}