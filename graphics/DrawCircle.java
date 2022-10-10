package graphics;

import javax.swing.*;
import java.awt.*;

public class DrawCircle extends JComponent {
  private ASTData treeData;
  private int NODE_WIDTH;
  private int NODE_HEIGHT;

  public DrawCircle(ASTData treeData, int NODE_HEIGHT, int NODE_WIDTH){
    this.treeData = treeData;
    this.NODE_WIDTH = NODE_WIDTH;
    this.NODE_HEIGHT = NODE_HEIGHT;
  }

  @Override
  public void paintComponent(Graphics graphics){
    Graphics2D graphics2D = (Graphics2D) graphics;
    graphics2D.setColor(Color.PINK);
    graphics2D.fillOval(treeData.getOffset() * NODE_WIDTH ,2*(treeData.getDepth() * NODE_WIDTH), NODE_WIDTH, NODE_HEIGHT);
    graphics2D.setColor(Color.BLACK);
    graphics2D.drawString(treeData.getTree().toString(), ((treeData.getOffset()*NODE_WIDTH)), (2*(treeData.getDepth()*NODE_HEIGHT)+(NODE_HEIGHT/2)));
  }

}
