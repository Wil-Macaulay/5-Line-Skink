package ca.celticmusic.skink;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

@SuppressWarnings("serial")

class TuneTreeRenderer extends DefaultTreeCellRenderer {
	private Color defaultForegroundColour;

	private Color defaultSelectionColour;

	private Color defaultBackgroundColour;

	private Color defaultBackSelectionColour;
	int debugLevel = 3;
	String debugString = "TuneTreeRenderer";

	public TuneTreeRenderer() {
		Debug.output(1, "TuneTreeRenderer c'tor");
		defaultForegroundColour = getTextNonSelectionColor();
		defaultSelectionColour = getTextSelectionColor();
		defaultBackgroundColour = getBackgroundNonSelectionColor();
		defaultBackSelectionColour = getBackgroundSelectionColor();
		Debug.output(1, "FG colour " + defaultForegroundColour);
		Debug.output(1, "Selected colour " + defaultSelectionColour);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Debug.output(debugLevel,debugString+ ".getTreeCellRendererComponent: selected?"+ sel);

		if (isError(value)) {
			Debug.output(debugLevel,debugString+ ".getTreeCellRendererComponent: error value, setting colours");
			setTextNonSelectionColor(Color.red);
			setTextSelectionColor(Color.yellow);
			setBackgroundNonSelectionColor(defaultBackgroundColour);
			setBackgroundSelectionColor(Color.red);
		} else {

				setTextNonSelectionColor(defaultForegroundColour);
				setTextSelectionColor(defaultSelectionColour);
				setBackgroundNonSelectionColor(defaultBackgroundColour);
				setBackgroundSelectionColor(defaultBackSelectionColour);
			}
		
		super.getTreeCellRendererComponent(tree, value, sel, expanded,
				leaf, row, hasFocus);
		return (this);
	}

	protected boolean isError(Object value) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object e = node.getUserObject();
		Debug.output(1, "user object" + e);
		if (!(e instanceof TuneTreeItem)) {
			Debug.output(1, "not a tune tree item");
			return false;
		}
		return (((TuneTreeItem) e).isErrorDetected());
	}

	
}

