/*
 * Written by Mike Wallace (mfwallace at gmail.com).  Available
 * on the web site http://mfwallace.googlepages.com/.
 * 
 * Copyright (c) 2006 Mike Wallace.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package io.miti.wordfinder;

/**
 * This class encapsulates a <code>Definition</code>, used to
 * contain a word, its part of speech and its definition.
 * 
 * @author Mike Wallace, 03 June 2004
 */
public final class Definition
{
  /**
   * The word itself.
   */
  private String strWord;
  
  /**
   * The part of speech (can be blank).
   */
  private String strSpeech;
  
  /**
   * The definition.
   */
  private String strDef;
  
  /**
   * Default constructor.
   */
  private Definition()
  {
    this("", "", "");
  }
  
  
  /**
   * Constructor.
   * 
   * @param inWord the word
   * @param inSpeech the part of speech
   * @param inDef the definition
   */
  public Definition(final String inWord,
                    final String inSpeech,
                    final String inDef)
  {
    this.strWord = inWord;
    this.strSpeech = inSpeech;
    this.strDef = inDef;
  }
  
  
  /**
   * Build a definition object from a line of input.
   * 
   * @param line the line of input
   * @return the Definition object
   */
  public static Definition buildFromLine(final String line)
  {
    // Check for a null or empty input
    if ((line == null) || (line.length() < 1))
    {
      return null;
    }
    
    // Save the length of the input
    int len = line.length();
    
    // Find the index of the first separator
    int i = line.indexOf('@');
    
    // Check for no separator or a separator at the end of the line
    if ((i <= 0) || (i >= (len - 1)))
    {
      return null;
    }
    
    // Save the first word as the term
    final String term = line.substring(0, i);
    
    // Find the index of the second separator
    int j = line.indexOf('@', i + 1);
    
    // Check for no separator or a separator at the end of the line
    if ((j <= 0) || (j >= (len - 1)))
    {
      return null;
    }
    
    // Save the second word (part of speech)
    final String speech = line.substring(i + 1, j);
    
    // Save the third word
    final String def = line.substring(j + 1);
    
    // Build the object and return it
    return new Definition(term, speech, def);
  }
  
  
  /**
   * Returns the object as a descriptive string.
   *
   * @return the object as a descriptive string
   */
  public String toString()
  {
    // This will hold the output string
    StringBuffer buf = new StringBuffer(150);
    
    // Build the string buffer
    buf.append("Word: ").append(strWord).append(" Speech:").append(strSpeech)
       .append(" Definition: ").append(strDef);
    
    // Return the buffer as a string
    return buf.toString();
  }
  
  
  /**
   * Returns the word.
   * 
   * @return the word
   */
  public String getWord()
  {
    return strWord;
  }
  
  
  /**
   * Returns the part of speech.
   * 
   * @return the part of speech
   */
  public String getSpeech()
  {
    return strSpeech;
  }
  
  
  /**
   * Returns the definition.
   * 
   * @return the definition
   */
  public String getDef()
  {
    return strDef;
  }
  
  
  /**
   * Returns the object in "dictionary" format.
   * 
   * @return the object in "dictionary" format
   */
  public String toLineString()
  {
    return toLineString(true);
  }
  
  
  /**
   * Returns the part of speech.
   * 
   * @return the part of speech
   */
  public String getFullPartOfSpeech()
  {
    if (strSpeech == null)
    {
      return "null";
    }
    else if (strSpeech.equals("n"))
    {
      return "Noun";
    }
    else if (strSpeech.equals("v"))
    {
      return "Verb";
    }
    else if (strSpeech.equals("a"))
    {
      return "Adjective";
    }
    else if (strSpeech.equals("r"))
    {
      return "Adverb";
    }
    
    return strSpeech;
  }
  
  
  /**
   * Returns an abbreviation for the part of speech.
   * 
   * @return an abbreviation for the part of speech
   */
  public String getAbbreviatedPartOfSpeech()
  {
    if (strSpeech == null)
    {
      return "null";
    }
    else if (strSpeech.equals("n"))
    {
      return "noun";
    }
    else if (strSpeech.equals("v"))
    {
      return "verb";
    }
    else if (strSpeech.equals("a"))
    {
      return "adj";
    }
    else if (strSpeech.equals("r"))
    {
      return "adv";
    }
    
    return strSpeech;
  }
  
  
  /**
   * Returns the object in "dictionary" format.
   * 
   * @param bIncludeWord whether to include the word in the output string
   * @return the object in "dictionary" format
   */
  public String toLineString(final boolean bIncludeWord)
  {
    // This will hold the output string
    StringBuffer buf = new StringBuffer(200);
    
    // Build the string.  Check if the user wants the word included.
    if (bIncludeWord)
    {
      buf.append(strWord).append(" : ");
    }
    
    // Add the part of speech, if it's not empty
    if (strSpeech.length() > 0)
    {
      buf.append("(").append(strSpeech).append(") ");
    }
    
    // Add the definition
    buf.append(strDef);
    
    // Return the buffer as a string
    return buf.toString();
  }
  
  
  /**
   * Returns the object as an XML string.
   * 
   * @return the object as an XML string
   */
  public String toXmlString()
  {
    // This will hold the output string
    StringBuffer buf = new StringBuffer(200);
    
    // Build the XML string.  The format will be:
    //   <w><v>word</v><p>part-of-speech</p><d>definition</d></w>
    buf.append("<w><v>").append(strWord).append("</v><p>").append(strSpeech)
       .append("</p><d>").append(strDef).append("</d></w>");
    
    // Return the buffer as a string
    return buf.toString();
  }
  
  
  /**
   * Returns the object as an "SML" string (SML is
   * the Simple Markup Language).  This is the format
   * the data is in when read by WordFinder.java.
   * The code for converting the dictionary from
   * HTML to SML uses this method.
   * 
   * @return the object as an SML string
   */
  public String toSmlString()
  {
    // This will hold the output string
    StringBuffer buf = new StringBuffer(200);
    
    // Build the string using @ as the separator.  The format will be:
    //   word@part-of-speech@definition
    buf.append(strWord).append("@");
    
    if (strSpeech.length() < 1)
    {
      buf.append(" ");
    }
    else
    {
      buf.append(strSpeech);
    }
    
    buf.append("@").append(strDef);
    
    // Return the buffer as a string
    return buf.toString();
  }
}
