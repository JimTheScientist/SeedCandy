package wearblackallday.gui.components.dungeonpanel;

import wearblackallday.swing.Events;
import wearblackallday.swing.components.GridPanel;
import wearblackallday.util.Dungeon;
import wearblackallday.util.Icons;

import javax.swing.*;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import static javax.swing.SwingUtilities.isRightMouseButton;

public class FloorPanel extends JPanel {
	protected FloorPanel() {
		super(new CardLayout());

		this.add(new GridPanel<>(9, 9, FloorButton::new), Dungeon.Size.NINE_BY_NINE.toString());
		this.add(new GridPanel<>(9, 7, FloorButton::new), Dungeon.Size.NINE_BY_SEVEN.toString());
		this.add(new GridPanel<>(7, 9, FloorButton::new), Dungeon.Size.SEVEN_BY_NINE.toString());
		this.add(new GridPanel<>(7, 7, FloorButton::new), Dungeon.Size.SEVEN_BY_SEVEN.toString());
	}

	protected double getBits() {
		double[] bits = {0D};

		for(int col = 0; col < this.currentGrid().getGridWidth(); col++) {
			this.currentGrid().forEachY(col, floorButton -> bits[0] += floorButton.getBits());
		}

		return bits[0];
	}

	protected String getString() {
		StringBuilder stringBuilder = new StringBuilder();

		for(int col = 0; col < this.currentGrid().getGridWidth(); col++) {
			this.currentGrid().forEachY(col, floorButton -> stringBuilder.append(floorButton.getString()));
		}

		return stringBuilder.toString();
	}

	protected void changeGrid(Dungeon.Size size) {
		((CardLayout)this.getLayout()).show(this, size.toString());
	}

	protected GridPanel<FloorButton> currentGrid() {
		for(Component c : this.getComponents()) {
			if(c.isVisible()) return (GridPanel<FloorButton>)c;
		}
		return null;
	}

	private class FloorButton extends JToggleButton {
		private FloorButton() {
			this.setPreferredSize(new Dimension(64, 64));
			this.setIcon(Icons.MOSSY);
			this.setSelectedIcon(Icons.COBBLE);
			this.setDisabledIcon(Icons.UNKNOWN);
			this.setDisabledSelectedIcon(Icons.UNKNOWN);
			this.setFocusable(false);
			this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
			this.addMouseListener(Events.Mouse.onClicked(e -> {
				if(isRightMouseButton(e)) {
					this.setEnabled(!this.isEnabled());
				}
				((DungeonPanel)FloorPanel.this.getParent()).updateInfo();
			}));
		}

		private double getBits() {
			return !this.isEnabled() ? 0D : this.isSelected() ? 2D : 0.415D;
		}

		private String getString() {
			return !this.isEnabled() ? "2" : this.isSelected() ? "0" : "1";
		}
	}
}
