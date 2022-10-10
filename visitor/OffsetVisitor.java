package visitor;

import ast.*;
import graphics.ASTData;

import java.util.HashMap;


public class OffsetVisitor extends ASTVisitor {

  private int currentOffset = 0;
  private int depth = 0;
  private int maxHeight;
  private int maxWidth;
  private int[] offsetTrackerArray = new int[100];
  private HashMap<AST, ASTData> hashMap;

  public OffsetVisitor(AST tree){
    this.hashMap = new HashMap<>();
    accept(tree);
    updateMaxDimensions();
  }

  public HashMap<AST, ASTData> getHashMap(){
    return this.hashMap;
  }

  public AST accept(AST tree){
    visitAndMap(tree);
    return null;
  }

  private void visitAndMap( AST tree ) {
    if (tree.kidCount() != 0) { //if tree has child Nodes
      depth++; //if tree has children increment depth
      if(tree.kidCount() == 1){
        accept(tree.getKid(1));
      } else {
        for (int i = 1; i <= tree.kidCount(); i++) {  //iterate through kids
          accept(tree.getKid(i)); //visit each child and executes this method
        }
      }
      depth--; //each time all kids visited return to parent depth
    }
    if(tree.kidCount() == 0){ //if no child Nodes (apply base case)
      currentOffset = offsetTrackerArray[depth];
    } else {  //if node has children
      getParentOffset(tree);
      currentOffset = offsetTrackerArray[depth];
    }
    hashMap.put(tree, new ASTData(tree, depth, currentOffset)); //key: tree    value: ASTData
    updateOffset(depth);
  }

  private void updateOffset(int depth) {
    offsetTrackerArray[depth] += 2;
  }

  private void getParentOffset(AST tree){
    if( ( hashMap.get((tree.getKid(tree.kidCount()))).getOffset() + hashMap.get(tree.getKid(1)).getOffset() )/2 < offsetTrackerArray[depth]){
      int shiftOffset =  offsetTrackerArray[depth] - ( ( hashMap.get((tree.getKid(tree.kidCount()))).getOffset() + hashMap.get(tree.getKid(1)).getOffset() )/ 2);
      reAdjustChildNodes(tree, shiftOffset);
    } else {
      offsetTrackerArray[depth] = (hashMap.get((tree.getKid(tree.kidCount()))).getOffset() + hashMap.get(tree.getKid(1)).getOffset() )/2;
    }
  }

  private void reAdjustChildNodes(AST tree, int shiftOffsetBy){
    if (tree.kidCount() != 0) { //revisit child Nodes edit ASTData
      if (tree.kidCount() == 1) {
        reAdjustChildNodes(tree.getKid(1), shiftOffsetBy);
      } else {
        for (int i = 1; i <= tree.kidCount(); i++) {
          reAdjustChildNodes(tree.getKid(i), shiftOffsetBy);
        }
      }
    }
    if(tree.kidCount() == 0) {
      if (hashMap.containsKey(tree)){
        hashMap.get(tree).setOffset(hashMap.get(tree).getOffset() + shiftOffsetBy);
        offsetTrackerArray[hashMap.get(tree).getDepth()] = hashMap.get(tree).getOffset() + 2;
      }
    }else if(tree.kidCount() == 1){
      if(hashMap.containsKey(tree)){ //if already in hashMap (ASTData has already been created)
        hashMap.get(tree).setOffset(hashMap.get(tree.getKid(1)).getOffset());
        offsetTrackerArray[hashMap.get(tree).getDepth()] = hashMap.get(tree).getOffset() + 2 ;
      }
    } else {
      if(hashMap.containsKey(tree)){
        hashMap.get(tree).setOffset((hashMap.get(tree.getKid(tree.kidCount())).getOffset() + hashMap.get(tree.getKid(1)).getOffset())/2);
        offsetTrackerArray[hashMap.get(tree).getDepth()] = hashMap.get(tree.getKid(tree.kidCount())).getOffset() + 2;
      }
    }
  }

  public void updateMaxDimensions() {
    int maxDepth = 0;
    int maxOffset = 0;
    for( AST tree : hashMap.keySet()){
      if(hashMap.get(tree).getDepth() > maxDepth){
        maxDepth = hashMap.get(tree).getDepth();
      }
      if(hashMap.get(tree).getOffset() > maxOffset){
        maxOffset = hashMap.get(tree).getOffset();
      }
    }
    this.maxHeight = maxDepth;
    this.maxWidth = maxOffset;
  }

  public int getMaxHeight() {
    return maxHeight;
  }

  public int getMaxWidth() {
    return maxWidth;
  }

  @Override
  public Object visitProgramTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitBlockTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitFunctionDeclTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitCallTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitDeclTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitIntTypeTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitBoolTypeTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitFormalsTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitActualArgsTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitIfTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitWhileTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitReturnTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitAssignTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitIntTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitIdTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitRelOpTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitAddOpTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitMultOpTree(AST t) {
    return accept(t);
  }

  @Override
  public Object visitSwitchTree(AST tree) {
    return accept(tree);
  }

  @Override
  public Object visitCaseTree(AST tree) {
    return accept(tree);
  }

  @Override
  public Object visitSwitchBlockTree(AST tree) {
    return accept(tree);
  }

  @Override
  public Object visitDefaultTree(AST tree) {
    return accept(tree);
  }

  @Override
  public Object visitUnlessTree(AST tree) {
    return accept(tree);
  }

  @Override
  public Object visitStringTypeTree(AST tree) {
    return accept(tree);
  }

  @Override
  public Object visitCharTree(AST tree) {
    return accept(tree);
  }

  @Override
  public Object visitStringLitTree(AST tree) {
    return accept(tree);
  }

  @Override
  public Object visitCharLitTree(AST tree) {
    return accept(tree);
  }

//  public static void main(String[] args) {
//    try {
//      OffsetVisitor offsetVisitor = new OffsetVisitor();
//
//      for(int i = 0; i <= offsetVisitor.getMaxHeight(); i++){
//        System.out.print(i+":\t");
//        for(int j = 0; j <= offsetVisitor.getMaxWidth(); j++){
//          if(offsetVisitor.treePositionMap[i][j] != null) {
//            System.out.print("X");
//          }else{
//            System.out.print(" ");
//          }
//        }
//        System.out.println();
//      }
//      System.out.println("max depth: "+ offsetVisitor.getMaxHeight());
//      System.out.println("max width: "+ offsetVisitor.getMaxWidth());
//
//      for (AST tree : offsetVisitor.hashMap.keySet()){
//        System.out.println("Tree: "+tree + "\tOffset: "+ offsetVisitor.hashMap.get(tree).getOffset() + "\tDepth: "+ offsetVisitor.hashMap.get(tree).getDepth());
//      }
//
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
}
