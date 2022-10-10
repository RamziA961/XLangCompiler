package visitor;

import ast.AST;
import graphics.ASTData;
import graphics.DrawCircle;
import graphics.DrawLine;
import java.awt.*;
import java.util.HashMap;
import java.awt.image.BufferedImage;


public class DrawOffsetVisitor extends ASTVisitor{
  private int maxWidth, maxHeight;
  private final int NODE_HEIGHT = 50;
  private final int NODE_WIDTH = 50;
  private HashMap<AST, ASTData> hashMapOfTree;
  private BufferedImage bufferedImage;
  private  Graphics2D graphics2D;

  public DrawOffsetVisitor(OffsetVisitor offsetVisitor){
    this.hashMapOfTree = offsetVisitor.getHashMap();
    this.maxHeight = (2 * ((1 + offsetVisitor.getMaxHeight()) * NODE_HEIGHT));
    this.maxWidth = (( 1 + offsetVisitor.getMaxWidth()) * NODE_WIDTH);
    bufferedImage = createBufferedImage();
  }

  public Object accept(AST tree, Graphics2D graphics2D){
    new DrawCircle( hashMapOfTree.get(tree), NODE_HEIGHT, NODE_WIDTH).paintComponent(graphics2D);
    if (tree.kidCount()!=0){
      for(AST child : tree.getKids()){
        new DrawLine(hashMapOfTree.get(tree), hashMapOfTree.get(child), NODE_HEIGHT, NODE_WIDTH).paintComponent(graphics2D);
      }
    }
    return null;
  }

  public void generateGraphics(Graphics2D graphics2D){
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setFont(new Font("Times New Roman", Font.BOLD, 12)); //text font and size
    graphics2D.setColor(Color.WHITE);
    graphics2D.fillRect(0,0, maxWidth, maxHeight); //background
    for (AST tree : hashMapOfTree.keySet()) {
      accept(tree, graphics2D);
    }
  }

  private BufferedImage createBufferedImage(){
    bufferedImage = new BufferedImage(maxWidth, maxHeight , BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics2D = bufferedImage.createGraphics(); //get bufferedImage graphics
    generateGraphics(graphics2D);
    graphics2D.dispose();
    return bufferedImage;
  }

  public BufferedImage getImage(){
    return bufferedImage;
  }

  @Override
  public Object visitProgramTree(AST t) {
    accept( t, graphics2D);
    return null;
  }

  @Override
  public Object visitBlockTree(AST t) {
    accept( t, graphics2D) ;
    return null;
  }

  @Override
  public Object visitFunctionDeclTree(AST t) {
    accept(t, graphics2D);
    return null;
  }

  @Override
  public Object visitCallTree(AST t) {
    accept( t, graphics2D);
    return null;
  }

  @Override
  public Object visitDeclTree(AST t) {
    accept( t, graphics2D);
    return null;
  }

  @Override
  public Object visitIntTypeTree(AST t) {
    accept(t, graphics2D);
    return null;
  }

  @Override
  public Object visitBoolTypeTree(AST t) {
    accept( t, graphics2D);
    return null;
  }

  @Override
  public Object visitFormalsTree(AST t) {
    accept( t , graphics2D);
    return null;
  }

  @Override
  public Object visitActualArgsTree(AST t) {
    accept( t,  graphics2D);
    return null;
  }

  @Override
  public Object visitIfTree(AST t) {
    accept(t, graphics2D);
    return null;
  }

  @Override
  public Object visitWhileTree(AST t) {
    accept( t , graphics2D);
    return null;
  }

  @Override
  public Object visitReturnTree(AST t) {
    accept( t, graphics2D);
    return null;
  }

  @Override
  public Object visitAssignTree(AST t) {
    accept( t, graphics2D);
    return null;
  }

  @Override
  public Object visitIntTree(AST t) {
    accept( t, graphics2D);
    return null;
  }

  @Override
  public Object visitIdTree(AST t) {
    accept( t, graphics2D);
    return null;
  }

  @Override
  public Object visitRelOpTree(AST t) {
    accept( t, graphics2D);
    return null;
  }

  @Override
  public Object visitAddOpTree(AST t) {
    accept(t, graphics2D);
    return null;
  }

  @Override
  public Object visitMultOpTree(AST t) {
    accept( t, graphics2D);
    return null;
  }

  @Override
  public Object visitSwitchTree(AST tree) {
    accept( tree, graphics2D);
    return null;
  }

  @Override
  public Object visitCaseTree(AST tree) {
    accept( tree, graphics2D);
    return null;
  }

  @Override
  public Object visitSwitchBlockTree(AST tree) {
    accept( tree,  graphics2D);
    return null;
  }

  @Override
  public Object visitDefaultTree(AST tree) {
    accept( tree, graphics2D);
    return null;
  }

  @Override
  public Object visitUnlessTree(AST tree) {
    accept(tree, graphics2D);
    return null;
  }

  @Override
  public Object visitStringTypeTree(AST tree) {
    accept(tree, graphics2D);
    return null;
  }

  @Override
  public Object visitCharTree(AST tree) {
    accept(tree, graphics2D);
    return null;
  }

  @Override
  public Object visitStringLitTree(AST tree) {
    accept(tree, graphics2D);
    return null;
  }

  @Override
  public Object visitCharLitTree(AST tree) {
    accept(tree, graphics2D);
    return null;
  }

//  public static void main(String[] args){
//    String sourceProgram = "src/sample_files/switchtest.x";
//    try {
//      DrawOffsetVisitor drawOffsetVisitor = new DrawOffsetVisitor(sourceProgram);
//      try {
//        File imagefile = new File(sourceProgram + ".png");
//        ImageIO.write(drawOffsetVisitor.getImage(), "png", imagefile);
//      } catch (Exception e) {
//        System.out.println("Error in saving image: " + e.getMessage());
//      }
//
//    } catch(Exception e){
//
//    }
//  }
}
