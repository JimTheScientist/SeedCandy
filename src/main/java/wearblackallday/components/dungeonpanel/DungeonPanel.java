package wearblackallday.gui.components.dungeonpanel;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.seedutils.mc.MCVersion;
import wearblackallday.data.Strings;
import wearblackallday.gui.components.TextBlock;
import wearblackallday.swing.components.LPanel;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.util.Dungeon;

import javax.swing.*;
import java.awt.BorderLayout;

public class DungeonPanel extends JPanel {
	private final FloorPanel floorPanel = new FloorPanel();
	private final JTextField dungeonString = new JTextField();
	private final JLabel bitLabel = new JLabel();
	private final SelectionBox<Dungeon.Size> sizeSelector = new SelectionBox<>(Dungeon.Size.values());

	public DungeonPanel() {
		this.dungeonString.setFont(this.dungeonString.getFont().deriveFont(16F));
		this.dungeonString.setHorizontalAlignment(JTextField.CENTER);
		TextBlock dungeonOutput = new TextBlock(false);

		this.sizeSelector.addActionListener(e -> {
			this.floorPanel.changeGrid(this.sizeSelector.getSelected());
			this.updateInfo();
		});

		SelectionBox<Biome> biomeSelector =
			new SelectionBox<>(DungeonPanel::getBiomeName, Biome.THE_VOID, Biome.DESERT, Biome.SWAMP, Biome.SWAMP_HILLS);
		SelectionBox<MCVersion> versionSelector =
			new SelectionBox<>(MCVersion.v1_16, MCVersion.v1_15, MCVersion.v1_14, MCVersion.v1_13);

		versionSelector.addActionListener(e ->
			biomeSelector.setEnabled(versionSelector.getSelected() == MCVersion.v1_16));

		JSplitPane userEntry = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
			new LPanel()
				.defaultSize(50, 25)
				.addComponent(this.sizeSelector)
				.addTextField("X", "x")
				.addTextField("Y", "y")
				.addTextField("Z", "z")
				.addComponent(versionSelector)
				.addComponent(biomeSelector)
				.addButton("crack", 80, 25, (panel, button, event) -> {
					int posX, posY, posZ;
					try {
						posX = Integer.parseInt(panel.getText("x").trim());
						posY = Integer.parseInt(panel.getText("y").trim());
						posZ = Integer.parseInt(panel.getText("z").trim());
					} catch(NumberFormatException exception) {
						return;
					}
					dungeonOutput.setText("");
					Dungeon.crack(this.dungeonString.getText(), posX, posY, posZ,
						versionSelector.getSelected(), biomeSelector.getSelected()).forEach(dungeonOutput::addEntry);
				})
				.addButton("copy", 80, 25, (panel, button, event) ->
					Strings.clipboard(dungeonOutput.getText()))
				.addComponent(this.bitLabel),
			this.dungeonString);

		this.setLayout(new BorderLayout());
		this.add(this.floorPanel, BorderLayout.CENTER);
		this.add(userEntry, BorderLayout.SOUTH);
		this.add(dungeonOutput, BorderLayout.EAST);
		this.setName("DungeonCracker");
		this.updateInfo();
	}

	protected void updateInfo() {
		this.bitLabel.setText("Bits: " + Math.round(this.floorPanel.getBits()));
		this.dungeonString.setText(this.floorPanel.getString());
	}

	private static String getBiomeName(Biome biome) {
		return biome == Biome.THE_VOID ? "other Biome" : biome.getName();
	}
}