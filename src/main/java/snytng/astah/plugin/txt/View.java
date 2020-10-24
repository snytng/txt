package snytng.astah.plugin.txt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import com.change_vision.jude.api.inf.view.IDiagramEditorSelectionEvent;
import com.change_vision.jude.api.inf.view.IDiagramEditorSelectionListener;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;
import com.change_vision.jude.api.inf.view.IEntitySelectionEvent;
import com.change_vision.jude.api.inf.view.IEntitySelectionListener;


public class View
extends
JPanel
implements
IPluginExtraTabView,
IEntitySelectionListener,
IDiagramEditorSelectionListener,
ProjectEventListener
{
	/**
	 * logger
	 */
	static final Logger logger = Logger.getLogger(View.class.getName());
	static {
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.CONFIG);
		logger.addHandler(consoleHandler);
		logger.setUseParentHandlers(false);
	}

	/**
	 * プロパティファイルの配置場所
	 */
	private static final String VIEW_PROPERTIES = "snytng.astah.plugin.txt.view";

	/**
	 * リソースバンドル
	 */
	private static final ResourceBundle VIEW_BUNDLE = ResourceBundle.getBundle(VIEW_PROPERTIES, Locale.getDefault());

	private String title = "<txt>";
	private String description = "<This plugin extracts strings from a diagram.>";

	private static final long serialVersionUID = 1L;
	private transient ProjectAccessor projectAccessor = null;
	private transient IDiagramViewManager diagramViewManager = null;

	public View() {
		try {
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			diagramViewManager = projectAccessor.getViewManager().getDiagramViewManager();
		} catch (Exception e){
			logger.log(Level.WARNING, e.getMessage(), e);
		}

		initProperties();
		initComponents();
	}

	private void initProperties() {
		try {
			title = VIEW_BUNDLE.getString("pluginExtraTabView.title");
			description = VIEW_BUNDLE.getString("pluginExtraTabView.description");
		}catch(Exception e){
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private void initComponents() {
		setLayout(new GridLayout(1,1));
		add(createPane());
	}

	JPanel textPanel = null;
	JTextArea s13nTextArea = null;
	JScrollPane textPanelScroll = null;
	JCheckBox copyAutomatically = null;

	JButton rightwardButton = null;
	JButton downwardButton = null;
	JButton leftwardButton = null;
	JButton upwardButton = null;
	JButton lastSelectedButton = rightwardButton;

	public Container createPane() {
		copyAutomatically = new JCheckBox("クリップボードへコピー");
		copyAutomatically.setSelected(false);
		copyAutomatically.addActionListener(e -> {
			if(copyAutomatically.isSelected()) {
				logger.log(Level.INFO, "copyAutomatically selected");
				copyTextToClipboard(s13nTextArea.getText());
			} else {
				logger.log(Level.INFO, "copyAutomatically unselected");
			}
		});

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

		rightwardButton = new JButton("→");
		rightwardButton.addActionListener(e -> {
			String text = S13n.getStrings(getPresentations(), S13n.Direction.RIGHTWARD);
			s13nTextArea.setText(text);
			s13nTextArea.setCaretPosition(0);

			if(copyAutomatically.isSelected()) {
				copyTextToClipboard(text);
			}

			lastSelectedButton.setForeground(Color.BLACK);
			lastSelectedButton = rightwardButton;
			lastSelectedButton.setForeground(Color.RED);
		});
		buttonsPanel.add(rightwardButton);

		downwardButton = new JButton("↓");
		downwardButton.addActionListener(e -> {
			String text = S13n.getStrings(getPresentations(), S13n.Direction.DOWNWARD);
			s13nTextArea.setText(text);
			s13nTextArea.setCaretPosition(0);

			if(copyAutomatically.isSelected()) {
				copyTextToClipboard(text);
			}

			lastSelectedButton.setForeground(Color.BLACK);
			lastSelectedButton = downwardButton;
			lastSelectedButton.setForeground(Color.RED);
		});
		buttonsPanel.add(downwardButton);

		leftwardButton = new JButton("←");
		leftwardButton.addActionListener(e -> {
			String text = S13n.getStrings(getPresentations(), S13n.Direction.LEFTWARD);
			s13nTextArea.setText(text);
			s13nTextArea.setCaretPosition(0);

			if(copyAutomatically.isSelected()) {
				copyTextToClipboard(text);
			}

			lastSelectedButton.setForeground(Color.BLACK);
			lastSelectedButton = leftwardButton;
			lastSelectedButton.setForeground(Color.RED);
		});
		buttonsPanel.add(leftwardButton);

		upwardButton = new JButton("↑");
		upwardButton.addActionListener(e -> {
			String text = S13n.getStrings(getPresentations(), S13n.Direction.UPWARD);
			s13nTextArea.setText(text);
			s13nTextArea.setCaretPosition(0);

			if(copyAutomatically.isSelected()) {
				copyTextToClipboard(text);
			}

			lastSelectedButton.setForeground(Color.BLACK);
			lastSelectedButton = upwardButton;
			lastSelectedButton.setForeground(Color.RED);
		});

		lastSelectedButton = rightwardButton;
		lastSelectedButton.setForeground(Color.RED);
		buttonsPanel.add(upwardButton);

		rightwardButton.setMnemonic(KeyEvent.VK_RIGHT);
		downwardButton.setMnemonic(KeyEvent.VK_DOWN);
		leftwardButton.setMnemonic(KeyEvent.VK_LEFT);
		upwardButton.setMnemonic(KeyEvent.VK_UP);

		textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanelScroll = new JScrollPane(
				textPanel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		s13nTextArea = new JTextArea();
		s13nTextArea.setEditable(false);
		textPanel.add(s13nTextArea);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(copyAutomatically, BorderLayout.NORTH);
		topPanel.add(buttonsPanel, BorderLayout.EAST);
		topPanel.add(textPanelScroll, BorderLayout.CENTER);

		return topPanel;
	}

	public IPresentation[] getPresentations() {
		IPresentation[] ps = diagramViewManager.getSelectedPresentations();
		if(ps.length == 0) {
			IDiagram d = diagramViewManager.getCurrentDiagram();
			if(d == null) {
				return new IPresentation[0];
			} else {
				try {
					return d.getPresentations();
				} catch (InvalidUsingException e) {
					return new IPresentation[0];
				}
			}
		} else {
			return ps;
		}
	}

	private static void copyTextToClipboard(String text) {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Clipboard clip = kit.getSystemClipboard();
		StringSelection ss = new StringSelection(text);
		clip.setContents(ss, ss);
	}

	/**
	 * 表示を更新する
	 */
	private void updateDiagramView() {
		try {
			logger.log(Level.INFO, "update diagram view.");

			// 自動更新
			lastSelectedButton.doClick();

		}catch(Exception e){
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	/**
	 * 図の選択が変更されたら表示を更新する
	 */
	@Override
	public void diagramSelectionChanged(IDiagramEditorSelectionEvent e) {
		updateDiagramView();
	}

	/**
	 * 要素の選択が変更されたら表示を更新する
	 */
	@Override
	public void entitySelectionChanged(IEntitySelectionEvent e) {
		updateDiagramView();
	}

	// ProjectEventListener
	/**
	 * 図が編集されたら表示を更新する
	 * @see com.change_vision.jude.api.inf.project.ProjectEventListener#projectChanged(com.change_vision.jude.api.inf.project.ProjectEvent)
	 */
	@Override
	public void projectChanged(ProjectEvent e) {
		updateDiagramView();
	}

	@Override
	public void projectClosed(ProjectEvent e) {
		// Do nothing
	}

	@Override
	public void projectOpened(ProjectEvent e) {
		// Do nothing
	}

	// IPluginExtraTabView
	@Override
	public void addSelectionListener(ISelectionListener listener) {
		// Do nothing
	}

	@Override
	public void activated() {
		// リスナーへの登録
		addDiagramListeners();
	}

	@Override
	public void deactivated() {
		// リスナーへの削除
		removeDiagramListeners();
	}

	private void addDiagramListeners(){
		diagramViewManager.addDiagramEditorSelectionListener(this);
		diagramViewManager.addEntitySelectionListener(this);
		projectAccessor.addProjectEventListener(this);
	}

	private void removeDiagramListeners(){
		diagramViewManager.removeDiagramEditorSelectionListener(this);
		diagramViewManager.removeEntitySelectionListener(this);
		projectAccessor.removeProjectEventListener(this);
	}


	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getTitle() {
		return title;
	}


}
