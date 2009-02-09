/*
*    jETeL/Clover - Java based ETL application framework.
*    Copyright (C) 2005-05  Javlin Consulting <info@javlinconsulting.cz>
*    
*    This library is free software; you can redistribute it and/or
*    modify it under the terms of the GNU Lesser General Public
*    License as published by the Free Software Foundation; either
*    version 2.1 of the License, or (at your option) any later version.
*    
*    This library is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    
*    Lesser General Public License for more details.
*    
*    You should have received a copy of the GNU Lesser General Public
*    License along with this library; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
*/
package org.jetel.util.string;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * In this class are stored rules for StringAproxComparator for different locale
 * Rules for given locale are stored in array of Strings: 
 * 	each String in the array have to be like "c1=c2=..=cn", where c1,c2,...,cn are chars which will be considered as equivalent (no white space is allowed)
 * After definition new rules don't forget to put it in the HashMap rules
 * 
* @author avackova <agata.vackova@javlinconsulting.cz> ; 
* (c) JavlinConsulting s.r.o.
*	www.javlinconsulting.cz
*	@created August 17, 2006 
 */
public class StringAproxComparatorLocaleRules {

	private static Map<String, String[]> rules=new HashMap<String, String[]>();

	private static final String[] CZ_RULES={
		"a=á=A=Á",
		"c=č=C=Č",
		"d=ď=D=Ď",
		"e=é=ě=E=É=Ě",
		"i=í=I=Í",
		"n=ň=N=Ň",
		"o=ó=O=Ó",
		"r=ř=R=Ř",
		"s=š=S=Š",
		"t=ť=T=Ť",
		"u=ů=ú=U=Ů=Ú",
		"y=ý=Y=Ý",
		"z=ž=Z=Ž"
		};
	
	private static final String[] PL_RULES={
		"a=ą=A=Ą",
		"c=ć=C=Ć",
		"e=ę=E=Ę",
		"l=ł=L=Ł",
		"n=ń=N=Ń",
		"o=ó=O=Ó",
		"s=ś=S=Ś",
		"z=ż=ź=Z=Ż=Ź"
	};

	private static final String[] ES_RULES={
		"a=á=A=Á",
		"e=é=E=É",
		"i=í=I=Í",
		"o=ó=O=Ó",
		"u=ú=ü=U=Ú=Ü",
		"n=ñ=N=Ñ"
	};

	private static final String[] FR_RULES={
		"a=à=â=A=À=Â",
		"e=è=é=ê=ë=E=È=É=Ê=Ë",
		"i=ï=I=Ï",
		"o=ô=O=Ô",
		"u=ù=û=ü=U=Ù=Û=Ü",
		"c=ç=C=Ç"
	};
	
	private static final String[] SK_RULES={
		"a=á=ä=A=Á=Ä",
		"c=č=C=Č",
		"d=ď=D=Ď",
		"e=é=E=É",
		"i=í=I=Í",
		"l=ĺ=ľ=L=Ĺ=Ľ",
		"n=ň=N=Ň",
		"o=ó=ô=O=Ó=Ô",
		"r=ŕ=R=Ŕ",
		"s=š=S=Š",
		"t=ť=T=Ť",
		"u=ú=U=Ú",
		"y=ý=Y=Ý",
		"z=ž=Z=Ž"
	};

	private static final String[] HU_RULES={
		"a=á=A=Á",
		"e=é=E=É",
		"i=í=I=Í",
		"o=ó=ö=ő=O=Ó=Ö=Ő",
		"u=ú=ü=ű=U=Ú=Ü=Ű"
	};

	/**
	 * Static initalization for rules HashMap
	 */
	static {
		rules.put("CZ",CZ_RULES);
		rules.put("CS",CZ_RULES);
		
		rules.put("AR",ES_RULES);
		rules.put("BO",ES_RULES);
		rules.put("CL",ES_RULES);
		rules.put("CO",ES_RULES);
		rules.put("CR",ES_RULES);
		rules.put("DO",ES_RULES);
		rules.put("SV",ES_RULES);
		rules.put("GT",ES_RULES);
		rules.put("HN",ES_RULES);
		rules.put("MX",ES_RULES);
		rules.put("NI",ES_RULES);
		rules.put("PA",ES_RULES);
		rules.put("PY",ES_RULES);
		rules.put("PE",ES_RULES);
		rules.put("PR",ES_RULES);
		rules.put("ES",ES_RULES);
		rules.put("UY",ES_RULES);
		rules.put("VE",ES_RULES);
		
		rules.put("BE",FR_RULES);
		rules.put("CA",FR_RULES);
		rules.put("FR",FR_RULES);
		rules.put("LU",FR_RULES);
		rules.put("CH",FR_RULES);
		
		rules.put("PL",PL_RULES);
		
		rules.put("HU",HU_RULES);

		rules.put("SK",SK_RULES);
	}
	
	
	/**
	 * Method for getting rules for given locale
	 * @param locale
	 * @return
	 * @throws NoSuchFieldException
	 */
	public static String getRules(String locale) throws NoSuchFieldException{
		String[] rule=(String[])rules.get(locale.substring(0,2).toUpperCase());
		if (rule!=null){
			StringBuffer result=new StringBuffer(6);
			for (int i=0;i<rule.length;i++){
				result.append("& ");
				result.append(rule[i]);
			}
			return result.toString();
		}else {
			throw new NoSuchFieldException("No field for this locale or wrong locale format");
		}
		
	}

	/**
	 * This method gets out avaible locale
	 */
	public static String[] getAvaibleLocales(){
		final Set<String> rulesSet = rules.keySet();
		return (String[]) rulesSet.toArray(new String[rulesSet.size()]);
	}
}
