import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;

public class Browser extends JFrame implements HyperlinkListener
{
	public static void main(String[] args) {
		Browser browser = new Browser();
		browser.setVisible(true);
	}
// back button
	private JButton backButton;

// address bar
	private JTextField addressBarField;

// display area
	private JEditorPane displayPane;

// page stack
	private Stack<URL> pageList = new Stack<URL>();

// constructor
	public Browser()
	{
// title
			super("My Browser");

// window size.
			setSize(1080, 720);

// exit on close
			addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					System.exit(0);
				}
			});
			
// tools panel
			JPanel toolPanel = new JPanel();
			backButton = new JButton("Back");
			backButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doBack();
				}
			});
			backButton.setEnabled(false);
			
			JButton goButton = new JButton("Go");
			goButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doGo();
				}
			});
			
			displayPane = new JEditorPane();
			displayPane.setContentType("text/html");
			displayPane.setEditable(false);
			displayPane.addHyperlinkListener(this);
			
			addressBarField = new JTextField(55);
			addressBarField.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					if ((!addressBarField.getText().equals("")) && (e.getKeyCode() == KeyEvent.VK_ENTER)) {
						doGo();
					}
				}
			});
			
			toolPanel.add(backButton);
			toolPanel.add(addressBarField);
			toolPanel.add(goButton);

			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(toolPanel, BorderLayout.NORTH);
			getContentPane().add(new JScrollPane(displayPane),
					BorderLayout.CENTER);
	}

// loads previous page
	private void doBack(){
		try {
			displayPane.setPage(checkUrl(pageList.pop().toString()));
			updateToolBar();
		} catch (Exception e) {}
	}

// loads page
	private void doGo() {
		URL verifiedUrl = checkUrl(addressBarField.getText());
		if (verifiedUrl != null) {
			showPage(verifiedUrl, true);
		} else {
			JOptionPane.showMessageDialog(this, "URL Error",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

// check url
	private URL checkUrl(String url) {
		if (!url.startsWith("http://")) {
			url = "http://" + url;
		}
		
		URL posturl = null;
		try {
			posturl = new URL(url);
		} catch (Exception e) {
			return null;
		}

		return posturl;
	}

// show requested page
	private void showPage(URL pageUrl,  boolean store)
	{
		try {
// store old url			
			try {
				URL newUrl = displayPane.getPage();
				pageList.push(newUrl);
			} 
			catch (Exception e) {}
			
// load page
			displayPane.setPage(pageUrl);
			
			updateToolBar();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, "Page did not load.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

// update tool bar on change
	private void updateToolBar() {
		addressBarField.setText(displayPane.getPage().toString());
		if (pageList.size() < 1) {
			backButton.setEnabled(false);
		} else {
			backButton.setEnabled(true);
		}
	}

// handle hyperlinks
	public void hyperlinkUpdate(HyperlinkEvent event) {
		HyperlinkEvent.EventType eventType = event.getEventType();
		if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
			if (event instanceof HTMLFrameHyperlinkEvent) {
				HTMLFrameHyperlinkEvent linkEvent =
						(HTMLFrameHyperlinkEvent) event;
				HTMLDocument document =
						(HTMLDocument) displayPane.getDocument();
				document.processHTMLFrameHyperlinkEvent(linkEvent);
			} else {
				showPage(event.getURL(), true);
			}
			pageList.push(event.getURL());
		}
	}
}