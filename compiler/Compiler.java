package compiler;

import ast.*;
import parser.Parser;
import visitor.*;

import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;

/**
 *  The Compiler class contains the main program for compiling
 *  a source program to bytecodes
 */
public class Compiler {

        /**
         * The Compiler class reads and compiles a source program
         */

    String sourceFile;

    public Compiler(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    void compileProgram() {
        try {
            System.out.println("---------------TOKENS-------------");
            Parser parser = new Parser(sourceFile);
            AST ast = parser.execute();

            PrintVisitor printVisitor = new PrintVisitor();
            ast.accept(printVisitor);

            OffsetVisitor ov = new OffsetVisitor(ast);
            // System.out.println( ov );

            DrawOffsetVisitor dv = new DrawOffsetVisitor(ov);

            try {
                File imagefile = new File(sourceFile + ".png");
                ImageIO.write(dv.getImage(), "png", imagefile);
            } catch (Exception e) {
                System.out.println("Error in saving image: " + e.getMessage());
            }

            final JFrame f = new JFrame("AST Tree : "+ sourceFile);
            f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    f.dispose();
                    System.exit(0);
                }
            });
            JLabel imagelabel = new JLabel(new ImageIcon(dv.getImage()));
            f.add("Center", imagelabel);
            f.pack();
            f.setSize(new Dimension(dv.getImage().getWidth() + 30, dv.getImage().getHeight() + 40));
            f.setResizable(false);
//            f.setLocationByPlatform(true);
            f.setVisible(true);
            f.repaint();
        }catch (Exception e) {
            System.out.println("********exception*******" + e.toString());
        };
    }

    public static void main(String args[]) {
        if (args.length == 0) {
            System.out.println("***Incorrect usage, try: java compiler.Compiler <file>");
            System.exit(1);
        }
        (new Compiler(args[0])).compileProgram();
    }
}