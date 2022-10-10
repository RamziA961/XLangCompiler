package graphics;

import javax.swing.*;
import java.awt.*;

public class DrawLine extends JComponent {
  private ASTData parentData;
  private ASTData childData;
  private int NODE_HEIGHT;
  private int NODE_WIDTH;

  public DrawLine(ASTData parentData, ASTData childData, int NODE_HEIGHT, int NODE_WIDTH){
    this.parentData = parentData;
    this.childData = childData;
    this.NODE_WIDTH = NODE_WIDTH;
    this.NODE_HEIGHT = NODE_HEIGHT;
  }

  @Override
  public void paintComponent (Graphics graphics){
    Graphics2D graphics2D = (Graphics2D) graphics;
    graphics.setColor(Color.BLACK);
    graphics2D.drawLine((parentData.getOffset()*NODE_WIDTH)+(NODE_WIDTH/2), (2*parentData.getDepth()+1)*NODE_HEIGHT , (childData.getOffset()*NODE_WIDTH)+(NODE_WIDTH/2), 2*childData.getDepth()*NODE_HEIGHT);
  }
}
