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

import io.miti.ui.component.Factory;
import io.miti.ui.panel.SimpleInternalFrame;
import io.miti.wordfinder.filter.AnagramFilter;
import io.miti.wordfinder.filter.ContainsFilter;
import io.miti.wordfinder.filter.RegexFilter;
import io.miti.wordfinder.filter.SoundFilter;
import io.miti.wordfinder.filter.TermFilter;
import io.miti.wordfinder.filter.WildcardFilter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * This class is the main class for the WordFinder application.
 * 
 * @author Mike Wallace, 03 June 2004
 */
public final class WordFinder implements ComponentListener, ItemListener
{
  /**
   * The application frame.
   */
  private JFrame m_appFrame = null;
  
  /**
   * The word to search for.
   */
  private JTextField tfWord = null;
  
  /**
   * The definition to search for.
   */
  private JTextField tfDef = null;
  
  /**
   * Radio button for a Wildcard search.
   */
  private JRadioButton btnWildcard = null;
  
  /**
   * Radio button for a RegEx search.
   */
  private JRadioButton btnRegex = null;
  
  /**
   * Radio button for a Soundex search.
   */
  private JRadioButton btnSoundex = null;
  
  /**
   * Radio button for an Anagram search.
   */
  private JRadioButton btnAnagram = null;
  
  /**
   * Radio button for a Contains search.
   */
  private JRadioButton btnContains = null;
  
  /**
   * Checkbox to limit the number of matches.
   */
  private JCheckBox cbLimit = null;
  
  /**
   * Checkbox to be case-sensitive.
   */
  private JCheckBox cbCase = null;
  
  /**
   * Text field that has the maximum number of matches
   * to return (enabled if cbLimit is checked).
   */
  private JTextField tfMaxValue = null;
  
  /**
   * This is the initial value for whether to be
   * case-sensitive on searches.
   */
  private static final boolean bInitialMatchCaseValue = false;
  
  /**
   * This is the initial value for whether to limit
   * the number of matches.
   */
  private static final boolean bInitialSetLimitValue = true;
  
  /**
   * This is the initial value for the maximum number
   * of matches to return.
   */
  private static final int nInitialLimitValue = 20;
  
  /**
   * The results table model.
   */
  private ResultsTableModel resultsModel = null;
  
  /**
   * The button used to search for a match by term.
   */
  private JButton btnSearchWord = null;
  
  /**
   * The button used to search for a match by definition.
   */
  private JButton btnSearchDef = null;
  
  /**
   * The results table.
   */
  private JTable tableResults = null;
  
  /**
   * The details panel.
   */
  private JPanel panelDetails = null;
  
  /**
   * The area used to show the selected term, part of speech, and definition.
   */
  private javax.swing.JTextPane paneDetails = null;
  
  /**
   * The search results.
   */
  private List<Definition> listData = null;  
  
  
  /**
   * Default constructor.
   */
  public WordFinder()
  {
    // Call the parent constructor
    super();
  }
  
  
  /**
   * Create the application's GUI.
   */
  public void createApp()
  {
    // Create and set up the window
    m_appFrame = new JFrame("WordFinder");
    m_appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    m_appFrame.addComponentListener(this);
    
    // Set the window size and center it
    m_appFrame.setMinimumSize(new Dimension(300, 300));
    m_appFrame.setPreferredSize(new Dimension(800, 600));
    m_appFrame.setSize(new Dimension(800, 600));
    centerOnScreen();
    
    // Generate the GUI and add it to the frame
    buildUI();
    
    // Display the window
    m_appFrame.pack();
    m_appFrame.setVisible(true);
    tfWord.requestFocusInWindow();
  }
  
  
  /**
   * Construct the user interface.
   */
  private void buildUI()
  {
    // Set up the right-side split pane (results, details)
    JSplitPane spRight = Factory.createStrippedSplitPane(
        JSplitPane.VERTICAL_SPLIT,
        initResultsPanel(),
        initDetailsPanel(),
        0.5f);    
    spRight.setDividerSize(3);
    spRight.setDividerLocation(300);
    spRight.setContinuousLayout(true);
    
    // Set up the split pane (search on the left, other split
    // pane on the right)
    JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            initSearchPanel(),
            spRight);
    sp.setDividerSize(3);
    sp.setDividerLocation(200);
    sp.setResizeWeight(0.0);
    sp.setContinuousLayout(true);
    
    // Set operation to do when user presses enter.
    // Default is Word-GO.  If focus is on Def, use Def-GO.
    m_appFrame.getRootPane().setDefaultButton(btnSearchWord);
    
    // Add the main panel to the content pane
    m_appFrame.getContentPane().add(sp, BorderLayout.CENTER);
  }
  
  
  /**
   * Initialize the Search panel.
   * 
   * @return the tabbed pane
   */
  public JComponent initSearchPanel()
  {
    JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
    // tabbedPane.putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE);
    tabbedPane.addTab("Search", Factory.createStrippedScrollPane(buildSearch()));
    tabbedPane.addTab("Options", Factory.createStrippedScrollPane(buildOptions()));
    
    // Set mnemonics for the tabs
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_E);
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_P);
    
    // Create the Search frame and add the tabbed pages
    SimpleInternalFrame sif = new SimpleInternalFrame("Search");
    sif.setPreferredSize(new Dimension(300, 500));
    sif.add(tabbedPane);
    
    return tabbedPane;
  }
  
  
  /**
   * Build the Search tabbed pane of the Search page.
   * 
   * @return the contents of the Search tab
   */
  private JComponent buildSearch()
  {
    // Build the panel for the pane
    JPanel panel = new JPanel(new GridBagLayout());
    
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(16, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    
    JLabel label1 = new JLabel("Find terms by word");
    panel.add(label1, c);
    
    c.insets = new Insets(4, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    
    tfWord = new JTextField(12);
    panel.add(tfWord, c);
    
    c.insets = new Insets(2, 3, 3, 3);
    c.gridx = 1;
    c.gridy = 1;
    
    btnSearchWord = new JButton("Go");
    btnSearchWord.setMnemonic(KeyEvent.VK_G);
    btnSearchWord.setToolTipText("Search for a match on the term");
    btnSearchWord.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        searchByWord(evt);
      }
    });
    panel.add(btnSearchWord, c);
    
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 2;
    
    JLabel sep = new JLabel("<html><u> &nbsp; &nbsp; &nbsp; &nbsp;" +
        " &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
        " &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
        " &nbsp; &nbsp;</u></html>");
    panel.add(sep, c);
    
    c.insets = new Insets(16, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 2;
    
    JLabel label2 = new JLabel("Find terms by definition");
    panel.add(label2, c);
    
    c.insets = new Insets(4, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 4;
    c.gridwidth = 1;
    
    tfDef = new JTextField(12);
    panel.add(tfDef, c);
    
    c.insets = new Insets(2, 3, 3, 3);
    c.gridx = 1;
    c.gridy = 4;
    
    btnSearchDef = new JButton("Go");
    btnSearchDef.setMnemonic(KeyEvent.VK_O);
    btnSearchDef.setToolTipText(
        "Search for a match on the term (optional) and definition");
    btnSearchDef.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        searchByDef(evt);
      }
    });
    panel.add(btnSearchDef, c);
    
    // Add the About button
    c.insets = new Insets(29, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 5;
    c.gridwidth = 2;
    
    JButton btnAbout = new JButton("About");
    btnAbout.setMnemonic(KeyEvent.VK_A);
    btnAbout.setToolTipText("Show information about the application");
    btnAbout.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        showAboutDialog(evt);
      }
    });
    panel.add(btnAbout, c);
    
    // Add the Quit button
    c.insets = new Insets(12, 3, 3, 3);
    c.gridx = 0;
    c.gridy = 6;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.NORTH;
    c.weighty = 1.0;
    
    JButton btnQuit = new JButton(" Quit ");
    btnQuit.setMnemonic(KeyEvent.VK_Q);
    btnQuit.setToolTipText("Quit the application");
    btnQuit.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final java.awt.event.ActionEvent evt)
      {
        exitApplication(evt);
      }
    });
    panel.add(btnQuit, c);
    
    // Return the panel
    return panel;
  }
  
  
  /**
   * Exit the application.
   * 
   * @param evt the event
   */
  private void exitApplication(final ActionEvent evt)
  {
    // The window closed, so exit the application
    Runtime.getRuntime().exit(0);
  }
  
  
  /**
   * Show the About dialog.
   * 
   * @param evt the event
   */
  private void showAboutDialog(final ActionEvent evt)
  {
    // Show the About dialog
    JOptionPane.showMessageDialog(m_appFrame, getAboutDialogText(),
        "About WordFinder", JOptionPane.INFORMATION_MESSAGE);
  }
  
  
  /**
   * Returns the text for the About box.
   * 
   * @return the text for the About box
   */
  private String getAboutDialogText()
  {
    // Build the text
    StringBuffer buf = new StringBuffer(200);
    
    buf.append("WordFinder: Search a dictionary for words. ");
    buf.append("Written by Mike Wallace, 2006.\n");
    buf.append("Released under the MIT license. Free for any use.\n");
    buf.append("Portions of the source code copyright JGoodies Karsten Lentzsch.\n");
    buf.append("The data is derived from the WordNet database: ");
    buf.append("http://wordnet.princeton.edu/\n");
    buf.append(
        "WordNet 2.1 Copyright 2005 by Princeton University. All rights reserved.");
    
    return buf.toString();
  }
  
  
  /**
   * Search by a word.
   * 
   * @param evt the event
   */
  private void searchByWord(final ActionEvent evt)
  {
    // Create the search filter for the term
    final TermFilter filter =
      getSearchFilter(tfWord.getText(), getCaseMatching());
    
    // Perform the search
    performSearch(filter, null);
  }
  
  
  /**
   * Search by a word and definition.
   * 
   * @param evt the event
   */
  private void searchByDef(final ActionEvent evt)
  {
    // Create the search filter for the term, based
    // on the selected option
    final TermFilter wordFilter =
      getSearchFilter(tfWord.getText(), getCaseMatching());
    
    // The definition filter is always a Contains filter
    final TermFilter defFilter =
      new ContainsFilter(tfDef.getText(), !getCaseMatching());
    
    // Perform the search
    performSearch(wordFilter, defFilter);
  }
  
  
  /**
   * Perform a search.
   * 
   * @param wordFilter the filter for the word
   * @param defFilter the filter for the definition
   */
  private void performSearch(final TermFilter wordFilter,
                             final TermFilter defFilter)
  {
    // Clear the list of results
    resultsModel.setRowData(null);
    resultsModel.fireTableDataChanged();
    
    if (listData != null)
    {
      listData.clear();
      listData = null;
    }
    
    // Clear the detail pane
    updateDetailsPane(0);
    
    // Get the current cursor
    java.awt.Cursor currCursor = m_appFrame.getCursor();
    
    // Set the Wait cursor
    m_appFrame.setCursor(java.awt.Cursor.getPredefinedCursor(
        java.awt.Cursor.WAIT_CURSOR));
    
    // Do the search
    searchMatches(wordFilter, defFilter);
    
    // Restore the cursor
    m_appFrame.setCursor(currCursor);
    
    // Check for no data
    if (listData == null)
    {
      return;
    }
    
    // Check the size and return if the data set is empty
    final int nSize = listData.size();
    if (nSize < 1)
    {
      return;
    }
    
    // Populate the listbox
    resultsModel.setRowData(listData);
    resultsModel.fireTableDataChanged();
    
    // Select the first item and update the detail pane
    ListSelectionModel lsm = tableResults.getSelectionModel();
    lsm.setAnchorSelectionIndex(0);
    lsm.setLeadSelectionIndex(0);
    
    // Make sure the first row is visible
    tableResults.scrollRectToVisible(tableResults.getCellRect(0, 0, true));
    
    // Set the focus on the table
    tableResults.requestFocusInWindow();
  }
  
  
  /**
   * Search for matches against the user's search criteria.
   * 
   * @param wordFilter the user's input string filter
   * @param defFilter the user's input definition filter
   */
  private void searchMatches(final TermFilter wordFilter,
                             final TermFilter defFilter)
  {
    // Get the search parameters
    final boolean bLimitCap = getMatchLimiting();
    final int nLimitCap = getMatchLimit();
    
    // Check the return limit (only if the checkbox
    // is selected)
    if ((bLimitCap) && (nLimitCap < 1))
    {
      // The user entered a limit less than one, so return
      return;
    }
    
    // Set up a list to hold the results
    listData = new ArrayList<Definition>(100);
    
    // Set up a counter to record how many hits we have so far
    int nCount = 0;
    
    // Declare the file reader
    BufferedReader in = null;
    
    // This is the name of the input file
    final String sInfile = "/dictionary.sml";
    
    // Read the file and search for matches
    try
    {
      // Read the dictionary file from the jar file
      java.io.InputStream is = getClass().getResourceAsStream(sInfile);
      if (is == null)
      {
        // The input file was not found
        JOptionPane.showMessageDialog(null,
            "Error: Unable to read the dictionary file (" + sInfile + ")");
      }
      else
      {
        // Open a reader to the input file stream
        in = new BufferedReader(new java.io.InputStreamReader(is));
        
        // Read each line from the input file and parse it
        String str;
        while ((str = in.readLine()) != null)
        {
          // Build the object from the line
          final Definition term = Definition.buildFromLine(str);
          
          // Check for a match
          if (matchOnSearch(wordFilter, defFilter, term))
          {
            // Add the match
            listData.add(term);
            ++nCount;
            
            // Check if we exceeded the limit
            if ((bLimitCap) && (nCount >= nLimitCap))
            {
              break;
            }
          }
        }
        
        // Close the input file
        in.close();
        in = null;
      }
    }
    catch (IOException ioe)
    {
      JOptionPane.showMessageDialog(null,
          "Error reading the dictionary file (" + sInfile + "): " +
          ioe.getMessage());
    }
    finally
    {
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch(IOException e)
        {
          in = null;
        }
        
        in = null;
      }
    }
  }
  
  
  /**
   * Returns whether the two terms match.
   * 
   * @param wordFilter the filter for the search word
   * @param defFilter the filter for the search definition
   * @param term the term from the data source
   * @return whether the term matches the user's search criteria
   */
  private boolean matchOnSearch(final TermFilter wordFilter,
                                final TermFilter defFilter,
                                final Definition term)
  {
    // Get the result of the word filter
    boolean result = wordFilter.accept(term.getWord());
    
    // Check the definition filter if there is one and
    // the word filter passed
    if ((result) && (defFilter != null))
    {
      result = defFilter.accept(term.getDef());
    }
    
    // Return the result
    return result;
  }


  /**
   * Build the Options tabbed pane of the Search page.
   * 
   * @return the contents of the Options tab
   */
  private JComponent buildOptions()
  {
    // Build the panel for the pane
    JPanel panel = new JPanel(new GridBagLayout());
    
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(12, 20, 4, 3);
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.WEST;
    c.weightx = 1.0;
    
    /*
     * Options:
     *   Match Case?
     *   Regular Expression?
     *   Limit Matches? Number?
     */
    cbCase = new JCheckBox("Match Case?");
    cbCase.setMnemonic(KeyEvent.VK_M);
    cbCase.setToolTipText(
        "Whether to respect the case of the search term (not the definition)");
    cbCase.setSelected(bInitialMatchCaseValue);
    panel.add(cbCase, c);
    
    c.insets = new Insets(3, 20, 0, 3);
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.WEST;
    
    // Add a checkbox to let a user limit matches
    cbLimit = new JCheckBox("Limit Matches?");
    cbLimit.setMnemonic(KeyEvent.VK_L);
    cbLimit.setToolTipText("Whether to limit the number of matches");
    cbLimit.setSelected(bInitialSetLimitValue);
    panel.add(cbLimit, c);
    cbLimit.addItemListener(this);
    
    // Add the Maximum Matches label
    JLabel label1 = new JLabel("Maximum: ");
    label1.setDisplayedMnemonic(KeyEvent.VK_X);
    
    c.insets = new Insets(0, 40, 3, 3);
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.WEST;
    
    // Add the text field to enter the max number of matches
    tfMaxValue = new JTextField(3);
    label1.setLabelFor(tfMaxValue);
    tfMaxValue.setToolTipText("The maximum number of matches to return");
    if (bInitialSetLimitValue)
    {
      tfMaxValue.setEnabled(true);
      tfMaxValue.setText(Integer.toString(nInitialLimitValue));
    }
    else
    {
      tfMaxValue.setEnabled(false);
      tfMaxValue.setText("");
    }
    
    // Create a gridlayout panel for this option
    JPanel panelMaxValue = new JPanel(new GridLayout(1, 2));
    panelMaxValue.add(label1);
    panelMaxValue.add(tfMaxValue);
    panel.add(panelMaxValue, c);
    
    // Add the Search Options group box
    JPanel subPanel = new JPanel(new GridLayout(0, 1));
    TitledBorder titledBorder =
      BorderFactory.createTitledBorder(
           BorderFactory.createLineBorder(java.awt.Color.black, 1),
           "Search Options");
    subPanel.setBorder(titledBorder);
    ButtonGroup bgOptions = new ButtonGroup();
    btnContains = new JRadioButton("Contains");
    btnAnagram = new JRadioButton("Anagram");
    btnWildcard = new JRadioButton("Wildcard (*, ?)");
    btnRegex = new JRadioButton("Regular Expression");
    btnSoundex = new JRadioButton("Soundex");
    
    btnContains.setMnemonic(KeyEvent.VK_C);
    btnContains.setToolTipText("Match if the data contains the search term");
    
    btnWildcard.setMnemonic(KeyEvent.VK_W);
    btnWildcard.setToolTipText("<html>Match on the search term using any combination " +
            "of '?'<br>(any one character) and '*' (any number of characters)");
    
    btnRegex.setMnemonic(KeyEvent.VK_R);
    btnRegex.setToolTipText("Match on the search term using a regular expression");
    
    btnSoundex.setMnemonic(KeyEvent.VK_S);
    btnSoundex.setToolTipText("Match on the search term for words that sound similar");
    
    btnAnagram.setMnemonic(KeyEvent.VK_A);
    btnAnagram.setToolTipText("Match if the data is an anagram of the search term");
    
    // Set the default (wildcard)
    btnContains.setSelected(true);
    
    bgOptions.add(btnContains);
    bgOptions.add(btnWildcard);
    bgOptions.add(btnRegex);
    bgOptions.add(btnSoundex);
    bgOptions.add(btnAnagram);
    
    subPanel.add(btnContains);
    subPanel.add(btnWildcard);
    subPanel.add(btnRegex);
    subPanel.add(btnSoundex);
    subPanel.add(btnAnagram);
    
    c.insets = new Insets(11, 25, 11, 3);
    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weighty = 1.0;
    c.weightx = 0.0;
    
    panel.add(subPanel, c);
    
    return panel;
  }
  
  
  /**
   * Notification that an item state changed.
   * 
   * @param e the item event
   */
  public void itemStateChanged(final ItemEvent e)
  {
    // Get the object that changed state
    Object source = e.getItemSelectable();
    
    // Check if the object is the checkbox
    if (source == cbLimit)
    {
      // Get the state of the checkbox
      boolean bSetting = cbLimit.isSelected();
      
      // Enable the text field if checked
      tfMaxValue.setEnabled(bSetting);
    }
  }
  
  
  /**
   * Initialize the Results panel.
   * 
   * @return the search panel
   */
  public JComponent initResultsPanel()
  {
    JPanel results = new JPanel(new BorderLayout());
    results.setMinimumSize(new Dimension(200, 200));
    results.setPreferredSize(new Dimension(500, 300));
    
    resultsModel = new ResultsTableModel();
    tableResults = new JTable(resultsModel);
    tableResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    // Ask to be notified of selection changes.
    ListSelectionModel rowSM = tableResults.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(final ListSelectionEvent e)
      {
        listSelectionChanged(e);
      }
    });

    
    JScrollPane scrollPane = new JScrollPane(tableResults);
    results.add(scrollPane);
    
    SimpleInternalFrame sif = new SimpleInternalFrame("Results");
    sif.setPreferredSize(new Dimension(300, 500));
    sif.add(results);
    
    return sif;
  }
  
  
  /**
   * Notification that the row changed.
   * 
   * @param e the event
   */
  private void listSelectionChanged(final ListSelectionEvent e)
  {
    //  Ignore extra messages.
    if (e.getValueIsAdjusting())
    {
      return;
    }
    
    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
    if (lsm.isSelectionEmpty())
    {
      return;
    }
    else
    {
      // Update the results page with info on the selected item
      int selectedRow = lsm.getMinSelectionIndex();
      updateDetailsPane(selectedRow);
    }
  }
  
  
  /**
   * Update the details pane.
   * 
   * @param nRow the row to update
   */
  private void updateDetailsPane(final int nRow)
  {
    // Remove the previous components in the details panel
    panelDetails.validate();
    
    if ((listData == null) || (nRow < 0))
    {
      paneDetails.setText("");
      return;
    }
    
    final int nSize = listData.size();
    if (nRow >= nSize)
    {
      paneDetails.setText("");
      return;
    }
    
    final Definition def = listData.get(nRow); 
    
    showDefinitionData(def);
    
    // Force a redraw of the window
    panelDetails.validate();
  }
  
  
  /**
   * Show the data for the selected term.
   * 
   * @param def the object to display
   */
  private void showDefinitionData(final Definition def)
  {
    // Populate the details pane with information on the selected object
    StringBuffer buf = new StringBuffer(200);
    // buf.append("<html>");
    buf.append(def.getWord()).append(" (").append(def.getSpeech())
       .append(") - ").append(def.getDef());
    // buf.append("</body></html>");
    
    // paneDetails.setText(buf.toString());
    
    paneDetails.setText("");
    StyledDocument doc = paneDetails.getStyledDocument();
    
    try
    {
      doc.insertString(doc.getLength(), def.getWord(), doc.getStyle("bold"));
      doc.insertString(doc.getLength(), " (", doc.getStyle("regular"));
      doc.insertString(doc.getLength(), def.getAbbreviatedPartOfSpeech(),
                       doc.getStyle("italic"));
      doc.insertString(doc.getLength(), ") - ", doc.getStyle("regular"));
      doc.insertString(doc.getLength(), def.getDef(), doc.getStyle("regular"));
    }
    catch (BadLocationException e)
    {
      // Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  
  /**
   * Initialize the Details panel.
   * 
   * @return the details panel
   */
  public JComponent initDetailsPanel()
  {
    panelDetails = new JPanel(new GridLayout(1, 1));
    panelDetails.setMinimumSize(new Dimension(200, 200));
    panelDetails.setPreferredSize(new Dimension(500, 300));
    
    paneDetails = new javax.swing.JTextPane();
    paneDetails.setOpaque(false);
    paneDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    StyledDocument doc = paneDetails.getStyledDocument();
    addStylesToDocument(doc);
    
    JScrollPane scrollPane = new JScrollPane(paneDetails);
    panelDetails.add(scrollPane);
    
    SimpleInternalFrame sif = new SimpleInternalFrame("Details");
    sif.setPreferredSize(new Dimension(300, 500));
    sif.add(panelDetails);
    
    return sif;
  }
  
  
  /**
   * Add styles to the document.
   * 
   * @param doc the StyledDocument to add styles to
   */
  protected void addStylesToDocument(final StyledDocument doc)
  {
    //Initialize some styles.
    Style def = StyleContext.getDefaultStyleContext().
                    getStyle(StyleContext.DEFAULT_STYLE);

    Style regular = doc.addStyle("regular", def);
    StyleConstants.setFontFamily(def, "SansSerif");
    StyleConstants.setFontSize(def, 16);
    StyleConstants.setLineSpacing(def, 2.0f);

    Style s = doc.addStyle("italic", regular);
    StyleConstants.setItalic(s, true);

    s = doc.addStyle("bold", regular);
    StyleConstants.setBold(s, true);

    s = doc.addStyle("small", regular);
    StyleConstants.setFontSize(s, 10);

    s = doc.addStyle("large", regular);
    StyleConstants.setFontSize(s, 18);
  }
  
  
  /**
   * Returns whether the user wants the search to be case-sensitive.
   * 
   * @return whether the user wants the search to be case-sensitive
   */
  private boolean getCaseMatching()
  {
    // Return whether to match case on a term
    return cbCase.isSelected();
  }
  
  
  /**
   * Returns whether the user wants to limit the number of matches.
   * 
   * @return whether the user wants to limit the number of matches
   */
  private boolean getMatchLimiting()
  {
    // Return whether to limit the number of matches 
    return cbLimit.isSelected();
  }
  
  
  /**
   * Returns the maximum number of matches, or -1 if there's no limit.
   * 
   * @return the maximum number of matches to allow
   */
  private int getMatchLimit()
  {
    // Get the maximum number of matches.
    int nLimit = -1;
    if (getMatchLimiting())
    {
      String sLimit = tfMaxValue.getText();
      if ((sLimit == null) || (sLimit.length() < 1))
      {
        return -1;
      }
      
      try
      {
        nLimit = Integer.parseInt(sLimit);
      }
      catch (NumberFormatException nfe)
      {
        nLimit = -1;
      }
    }
    
    return nLimit;
  }
  
  
  /**
   * Returns the search filter.
   * 
   * @param term the search term
   * @param matchCase whether to match on case
   * @return the search filter
   */
  private TermFilter getSearchFilter(final String term,
                                     final boolean matchCase)
  {
    TermFilter tf = null;
    
    if (btnRegex.isSelected())
    {
      tf = new RegexFilter(term, !matchCase);
    }
    else if (btnAnagram.isSelected())
    {
      tf = new AnagramFilter(term, !matchCase);
    }
    else if (btnSoundex.isSelected())
    {
      tf = new SoundFilter(term, !matchCase);
    }
    else if (btnWildcard.isSelected())
    {
      tf = new WildcardFilter(term, !matchCase);
    }
    else if (btnContains.isSelected())
    {
      tf = new ContainsFilter(term, !matchCase);
    }
    
    return tf;
  }
  
  
  /**
   * Center the application on the screen.
   */
  public void centerOnScreen()
  {
    // Get the size of the screen
    Dimension screenDim = java.awt.Toolkit.getDefaultToolkit()
            .getScreenSize();

    // Determine the new location of the window
    int x = (screenDim.width - m_appFrame.getSize().width) / 2;
    int y = (screenDim.height - m_appFrame.getSize().height) / 2;

    // Move the window
    m_appFrame.setLocation(x, y);
  }
  
  
  /**
   * Handle the component getting resized.
   * 
   * @param e the component event
   */
  public void componentResized(final ComponentEvent e)
  {
    // Get the current window size
    Dimension d = m_appFrame.getSize();
    int nWidth = (int) d.getWidth();
    int nHeight = (int) d.getHeight();
    
    // Default to not resizing
    boolean bResize = false;
    
    // Check the width to see if it's below the minimum
    if (nWidth < 500)
    {
      // It is, so modify the value and record that we want to resize
      nWidth = 500;
      bResize = true;
    }
    
    // Check the height to see if it's below the minimum
    if (nHeight < 450)
    {
      // It is, so modify the value and record that we want to resize
      nHeight = 450;
      bResize = true;
    }
    
    // Check if we need to resize the window
    if (bResize)
    {
      // We do, so set the new size
      m_appFrame.setSize(nWidth, nHeight);
    }
  }
  
  
  /**
   * Handle the component getting moved.
   * 
   * @param e the component event
   */
  public void componentMoved(final ComponentEvent e)
  {
    // Nothing to do here
  }
  
  
  /**
   * Handle the component getting shown.
   * 
   * @param e the component event
   */
  public void componentShown(final ComponentEvent e)
  {
    // Nothing to do here
  }
  
  
  /**
   * Handle the component getting hidden.
   * 
   * @param e the component event
   */
  public void componentHidden(final ComponentEvent e)
  {
    // Nothing to do here
  }
  
  
  /**
   * Writes an error message to standard error.
   * 
   * @param line the error message
   */
  public void writeErr(final String line)
  {
    System.err.println(line);
  }
  
  
  /**
   * Use the default look and feel.
   */
  private static void initLookAndFeel()
  {
    // Use this system's look and feel
    try
    {
      javax.swing.UIManager.setLookAndFeel(
        javax.swing.UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e)
    {
      System.out.println("Exception: " + e.getMessage());
    }
  }
  
  
  /**
   * Initialize the look and feel, instantiate the app, and run it.
   */
  private static void createAndRun()
  {
    initLookAndFeel();

    WordFinder app = new WordFinder();
    app.createApp();
  }
  
  
  /**
   * Make the application compatible with Apple Macs.
   * 
   * @param appName the name of the application
   */
  public static void makeMacCompatible(final String appName)
  {
    // Set the system properties that a Mac uses
    System.setProperty("apple.awt.brushMetalLook", "true");
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.showGrowBox", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
  }
  
  
  /**
   * Main method for the application.
   * 
   * @param args command-line arguments
   */
  public static void main(final String[] args)
  {
    // Set up the Mac-related properties
    makeMacCompatible("WordFinder");
    
    // Schedule a job for the event-dispatching thread
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        createAndRun();
      }
    });
  }
}
