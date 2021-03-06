/*
 * Copyright (c) 2010 Commsen International. All rights reserved.
 * 
 * This file is part of JWebThumb library.
 *	
 * JWebThumb library is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * JWebThumb library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with JWebThumb library.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 */

package com.commsen.jwebthumb.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.commsen.jwebthumb.WebThumbCredits;
import com.commsen.jwebthumb.WebThumbJob;
import com.commsen.jwebthumb.WebThumbJobStatus;
import com.commsen.jwebthumb.WebThumbResponse;
import com.commsen.jwebthumb.WebThumbStatus;
import com.commsen.jwebthumb.simplexml.SimpleXmlSerializer;

/**
 * @author <a href="mailto:MilenDyankov@gmail.com">Milen Dyankov</a>
 * 
 */
public class XMLParsingTest {

	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	/**
	 * Tests the conversion of XML to {@link WebThumbResponse} containing list of
	 * {@link WebThumbJob}
	 * 
	 * @throws ParseException
	 */
	@Test
	public void parseWebThumbResponse() throws ParseException {
		String response = "<webthumb>\n" + // 
		        "	<jobs>\n" + //
		        "		<job estimate='20' time='2008-02-27 16:49:48' url='http://blog.joshuaeichorn.com' cost='1'>wt47c5f71c37c3a</job>\n" + // 
		        "		<job estimate='38' time='2008-02-27 16:49:48' url='http://webthumb.bluga.net' cost='2'>wt47c5f71c3b6d2</job>\n" + //
		        "	</jobs>\n" + //
		        "</webthumb>";

		WebThumbResponse webThumbResponse = SimpleXmlSerializer.parseResponse(response, WebThumbResponse.class);
		System.out.println(webThumbResponse);

		List<WebThumbJob> jobs = webThumbResponse.getJobs();
		Assert.assertEquals(2, jobs.size());

		WebThumbJob job = jobs.get(0);
		Assert.assertEquals(20, job.getEstimate());
		Assert.assertEquals("http://blog.joshuaeichorn.com", job.getUrl());
		Assert.assertEquals(1, job.getCost());
		Assert.assertEquals("wt47c5f71c37c3a", job.getId());
		Assert.assertEquals(timeFormat.parse("2008-02-27 16:49:48"), job.getTime());

		job = jobs.get(1);
		Assert.assertEquals(38, job.getEstimate());
		Assert.assertEquals("http://webthumb.bluga.net", job.getUrl());
		Assert.assertEquals(2, job.getCost());
		Assert.assertEquals("wt47c5f71c3b6d2", job.getId());
	}


	/**
	 * Tests the conversion of XML to {@link WebThumbResponse} containing {@link WebThumbCredits}
	 * 
	 * @throws ParseException
	 */
	@Test
	public void parseWebThumbCredits() throws ParseException {
		String response = "<webthumb>\n" + //
		        "	<credits>\n" + //
		        "		<used-this-month>1642.5</used-this-month>\n" + //
		        "		<easythumb-cached-this-month>15765</easythumb-cached-this-month>\n" + //
		        "		<subscription>1000</subscription>\n" + //
		        "		<reserve>45587.1</reserve>\n" + //
		        "	</credits>\n" + //
		        "</webthumb>";//

		WebThumbResponse webThumbResponse = SimpleXmlSerializer.parseResponse(response, WebThumbResponse.class);
		System.out.println(webThumbResponse);

		WebThumbCredits credits = webThumbResponse.getCredits();
		Assert.assertNotNull(credits);

		Assert.assertEquals(1642.5d, credits.getUsedThisMonth().doubleValue(), 0.01);
		Assert.assertEquals(15765d, credits.getCachedThisMonth().doubleValue(), 0.01);
		Assert.assertEquals(1000, credits.getSubscription().intValue());
		Assert.assertEquals(45587.1d, credits.getReserve().doubleValue(), 0.01);
	}


	/**
	 * Tests the conversion of XML to {@link WebThumbResponse} containing list of
	 * {@link WebThumbJobStatus}
	 * 
	 * @throws ParseException
	 */
	@Test
	public void parseWebThumbJobStatusResponse() throws ParseException {
		String response = "<webthumb>\n"
		        + "	<jobStatus>\n"
		        + "		<status id='wt47c5fcba65776' submissionTime='2008-02-27 17:13:46' browserWidth='1024' browserHeight='768' inProcess='1' ></status>\n"
		        + "		<status id='wt47c5f71c37c3a' submissionTime='2008-02-27 16:49:48' browserWidth='1024' browserHeight='768' pickup='http://webthumb.bluga.net/data/3a/7c/c3/wt47c5f71c37c3a.zip' completionTime='2008-02-27 16:50:01'>Complete</status>\n"
		        + "	</jobStatus>\n" + //
		        "</webthumb>";

		WebThumbResponse webThumbResponse = SimpleXmlSerializer.parseResponse(response, WebThumbResponse.class);
		System.out.println(webThumbResponse);

		List<WebThumbStatus> jobs = webThumbResponse.getJobStatus().getStatuses();
		Assert.assertEquals(2, jobs.size());

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2008);
		cal.set(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 27);
		cal.set(Calendar.HOUR_OF_DAY, 17);
		cal.set(Calendar.MINUTE, 13);
		cal.set(Calendar.SECOND, 46);
		cal.set(Calendar.MILLISECOND, 0);

		WebThumbStatus jobStatus = jobs.get(0);
		Assert.assertEquals(768, jobStatus.getBrowserHeight());
		Assert.assertEquals(1024, jobStatus.getBrowserWidth());
		Assert.assertEquals(cal.getTime(), jobStatus.getSubmissionTime());
		Assert.assertTrue(jobStatus.isInProcess());
		Assert.assertFalse(jobStatus.isCompleted());
		Assert.assertEquals("wt47c5fcba65776", jobStatus.getId());
		Assert.assertNull(jobStatus.getPickupURL());
		Assert.assertNull(jobStatus.getCompletionTime());

		jobStatus = jobs.get(1);
		Assert.assertEquals(768, jobStatus.getBrowserHeight());
		Assert.assertEquals(1024, jobStatus.getBrowserWidth());
		cal.set(Calendar.HOUR_OF_DAY, 16);
		cal.set(Calendar.MINUTE, 49);
		cal.set(Calendar.SECOND, 48);
		Assert.assertEquals(cal.getTime(), jobStatus.getSubmissionTime());
		Assert.assertFalse(jobStatus.isInProcess());
		Assert.assertTrue(jobStatus.isCompleted());
		Assert.assertEquals("wt47c5f71c37c3a", jobStatus.getId());
		Assert.assertEquals("http://webthumb.bluga.net/data/3a/7c/c3/wt47c5f71c37c3a.zip", jobStatus.getPickupURL());
		cal.set(Calendar.HOUR_OF_DAY, 16);
		cal.set(Calendar.MINUTE, 50);
		cal.set(Calendar.SECOND, 01);
		Assert.assertEquals(cal.getTime(), jobStatus.getCompletionTime());
	}

}
