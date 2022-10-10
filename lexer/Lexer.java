package lexer;

/**
 *  The Lexer class is responsible for scanning the source file
 *  which is a stream of characters and returning a stream of
 *  tokens; each token object will contain the string (or access
 *  to the string) that describes the token along with an
 *  indication of its location in the source program to be used
 *  for error reporting; we are tracking line numbers; white spaces
 *  are space, tab, newlines
 */
public class Lexer {
  private boolean atEOF = false;
  // next character to process
  private char ch;
  private SourceReader source;

  // positions in line of current token
  private int startPosition, endPosition;
  private int lineNumber;
  /**
   *  Lexer constructor
   * @param sourceFile is the name of the File to read the program source from
   */
  public Lexer( String sourceFile ) throws Exception {
    // init token table
    new TokenType();
    source = new SourceReader( sourceFile );
    ch = source.read();
  }

  /**
   *  newIdTokens are either ids or reserved words; new id's will be inserted
   *  in the symbol table with an indication that they are id's
   *  @param id is the String just scanned - it's either an id or reserved word
   *  @param startPosition is the column in the source file where the token begins
   *  @param endPosition is the column in the source file where the token ends
   *  @return the Token; either an id or one for the reserved words
   */
  public Token newIdToken( String id, int startPosition, int endPosition) { //line number added
    return new Token(startPosition, endPosition, source.getLineNumber(), Symbol.symbol( id, Tokens.Identifier ) );
  }

  /**
   *  number tokens are inserted in the symbol table; we don't convert the
   *  numeric strings to numbers until we load the bytecodes for interpreting;
   *  this ensures that any machine numeric dependencies are deferred
   *  until we actually run the program; i.e. the numeric constraints of the
   *  hardware used to compile the source program are not used
   *  @param number is the int String just scanned
   *  @param startPosition is the column in the source file where the int begins
   *  @param endPosition is the column in the source file where the int ends
   *  @return the int Token
   */
  public Token newNumberToken( String number, int startPosition, int endPosition ) { //line number added
    return new Token(startPosition, endPosition, source.getLineNumber(), Symbol.symbol( number, Tokens.INTeger));
  }


  /**
   *  Method to create StringLit tokens. Characters contained between two double quotes (inclusive)
   *  are StringLiterals.
   *  @param stringLiteral is the String just scanned
   *  @param startPosition is the column in the source file where the stringLit begins
   *  @param endPosition is the column in the source file where the stringLit ends
   *  @return the stringLit Token
   */
  public Token newStringLitToken (String stringLiteral, int startPosition, int endPosition){
    return new Token(startPosition, endPosition, source.getLineNumber(), Symbol.symbol(stringLiteral, Tokens.StringLit));
  }
  public Token newStringLitToken (String stringLiteral, int startPosition, int endPosition, int lineNumber){
    return new Token(startPosition, endPosition, lineNumber, Symbol.symbol(stringLiteral, Tokens.StringLit));
  }

  /**
   *  Method to create CharLit tokens. Character contained between two single quotes (inclusive)
   *  are CharLiterals.
   *  @param charLiteral is the int charLit just scanned
   *  @param startPosition is the column in the source file where the charLit begins
   *  @param endPosition is the column in the source file where the charLit ends
   *  @return the charLit Token
   */
  public Token newCharLitToken (String charLiteral, int startPosition, int endPosition){
    return new Token(startPosition, endPosition, source.getLineNumber(), Symbol.symbol(charLiteral, Tokens.CharLit));
  }
  public Token newCharLitToken (String charLiteral, int startPosition, int endPosition, int lineNumber){
    return new Token(startPosition, endPosition, lineNumber, Symbol.symbol(charLiteral, Tokens.CharLit));
  }

  /**
   *  build the token for operators (+ -) or separators (parens, braces)
   *  filter out comments which begin with two slashes
   *  @param s is the String representing the token
   *  @param startPosition is the column in the source file where the token begins
   *  @param endPosition is the column in the source file where the token ends
   *  @return the Token just found
   */
  public Token makeToken( String s, int startPosition, int endPosition ) {
    // filter comments

    if( s.equals("//") ) {
      try {
        int oldLine = source.getLineNumber();

        do {
          ch = source.read();
        } while( oldLine == source.getLineNumber() );
      } catch (Exception e) {
        atEOF = true;
      }

      return nextToken();
    }

    // ensure it's a valid token
    Symbol sym = Symbol.symbol( s, Tokens.BogusToken );

    if( sym == null ) {
      System.out.println( "******** illegal character: " + s );
      atEOF = true;
      return nextToken();
    }

    return new Token( startPosition, endPosition, lineNumber, sym );
  }

  /**
   *  @return the next Token found in the source file
   */
  public Token nextToken() {
    // ch is always the next char to process
    if( atEOF ) {
      if( source != null ) {
        source.close();
//        source = null; // might need to remove this line
      }

      return null;
    }

    try {
      // scan past whitespace
      while( Character.isWhitespace( ch )) {
        ch = source.read();
      }
    } catch( Exception e ) {
      atEOF = true;
      return nextToken();
    }

    startPosition = source.getPosition();
    endPosition = startPosition - 1;
    lineNumber = source.getLineNumber(); //added to include line number in lexer output

    if( Character.isJavaIdentifierStart( ch )) {
      // return tokens for ids and reserved words
      String id = "";

      try {
        do {
          endPosition++;
          id += ch;
          ch = source.read();
        } while( Character.isJavaIdentifierPart( ch ));
      } catch( Exception e ) {
        atEOF = true;
      }

      return newIdToken( id, startPosition, endPosition); //Line number added
    }

    if( Character.isDigit( ch )) {
      // return number tokens
      String number = "";

      try {
        do {
          endPosition++;
          number += ch;
          ch = source.read();
        } while( Character.isDigit( ch ));
      } catch( Exception e ) {
        atEOF = true;
      }

      return newNumberToken( number, startPosition, endPosition);
    }

    //checks if ch is a double quote. If so, adds ch to string and loops till next double quote is found.
    if(ch == '\"'){
      //return stringLiteral tokens
      String stringLiteral = ""; //String for stringLiteral
      String errorLiteral = ""; //String for invalid instantiation
      int stringStartLine = source.getLineNumber(); //line where stringLiteral starts
      try {
        do {
          if(stringStartLine == source.getLineNumber()) {
            endPosition++;
            stringLiteral += ch;
          }else if(stringStartLine != source.getLineNumber()){ //if string literal continues to next line
            errorLiteral += ch;
          }
          ch = source.read(); //get next character
        } while (ch != '\"'); //stops when double quote found.

      }catch(Exception e){
        atEOF = true;
        if(ch!= '\"') { //if end of file reached and double quote not found.
          System.out.println("******** illegal characters: "+ stringLiteral);
        }
      }
      ch = ' '; //when the program break out of the loop, ch = " so it is reassigned to a space character
      return makeStringLiteralToken(stringLiteral, errorLiteral, startPosition, endPosition, lineNumber);
    }

    //checks if ch is a single quote. If so, adds ch to string and loops till next single quote is found.
    //returns charLiteralToken
    if(ch == '\''){
      //return charLiteral tokens
      String charLiteral = "";
      String errorLiteral = "";
      int startingLineNumber = source.getLineNumber();
      charLiteral += ch;
      endPosition++;
      try {
        do{
          ch = source.read();
          if(lineNumber != source.getLineNumber() || charLiteral.length() > 3) {
            errorLiteral += ch;
          }else{
            charLiteral += ch;
            endPosition++;
          }
        }while(ch != '\'');
        ch = ' ';  //when the program break out of the loop, ch = " so it is reassigned to a space character

      }catch(Exception e){
        atEOF = true;
      }
      return makeCharLiteralToken(charLiteral, errorLiteral, startPosition, endPosition, startingLineNumber);

    }


    // At this point the only tokens to check for are one or two
    // characters; we must also check for comments that begin with
    // 2 slashes
    String charOld = "" + ch;
    String op = charOld;
    Symbol sym;
    try {
      endPosition++;
      ch = source.read();
      op += ch;

      // check if valid 2 char operator; if it's not in the symbol
      // table then don't insert it since we really have a one char
      // token
      sym = Symbol.symbol( op, Tokens.BogusToken );
      if (sym == null) {
        // it must be a one char token
        return makeToken( charOld, startPosition, endPosition);
      }

      endPosition++;
      ch = source.read();

      return makeToken( op, startPosition, endPosition );
    } catch( Exception e ) { /* no-op */ }

    atEOF = true;
    if( startPosition == endPosition ) {
      op = charOld;
    }

    return makeToken( op, startPosition, endPosition );
  }
  //added methods----------------------------------
  /**
  * method to create charLiteralTokens from supplied parameters character checkLiteralCheck
   * @param charLiteralString string that contains the first three characters from single quote (inclusive)
   * @param errorLiteral string that contains invalid characters
   * @param startPosition position of first single quote
   * @param endPosition position of last character in charLiteralString
   * @return newCharLitToken
   */

  //broken
  public Token makeCharLiteralToken (String charLiteralString, String errorLiteral, int startPosition, int endPosition, int lineNumber){
     if(source.getLineNumber() != lineNumber){
       charLiteralString = charLiteralString.trim();
       if( charLiteralString.length() == 2) {
         System.out.println("******** illegal character:" + errorLiteral);
         charLiteralString = charLiteralString + "'";
       }else if(charLiteralString.length() == 1){
         charLiteralString += errorLiteral.charAt(0) + "'";
         System.out.println("******** illegal character:" + errorLiteral);
       }
    }
     if(charLiteralString.length() > 3){
       charLiteralString = charLiteralString.substring(0,1) + "'";
       System.out.println("******** illegal character:" + errorLiteral);
     }else if(charLiteralString.length() == 2){
       System.out.println("******** illegal character:" + charLiteralString);
       endPosition++;
       charLiteralString += "'";
     }
    return newCharLitToken(charLiteralString, startPosition, endPosition, lineNumber);
  }
  /**
   * method to create charLiteralTokens from supplied parameters character checkLiteralCheck
   * @param stringLiteralString string that contains the all three characters from double quote (inclusive)
   *                            to end of line or second double quote.
   * @param errorLiteral string that contains invalid characters
   * @param startPosition position of first single quote
   * @param endPosition position of last character in stringLiteralString
   * @param lineNumber line number that first double quote was encountered
   * @return newStringLitToken
   */
  public Token makeStringLiteralToken(String stringLiteralString, String errorLiteral, int startPosition, int endPosition, int lineNumber){
    if(!errorLiteral.isEmpty()){
      stringLiteralString = stringLiteralString.substring(0, stringLiteralString.length()-1);
      System.out.println("******** illegal character: "+ stringLiteralString);
      System.out.println("******** illegal character: "+ errorLiteral + "\"");
      endPosition--;
    }
    stringLiteralString += "\"";
    endPosition++;
    return newStringLitToken(stringLiteralString, startPosition, endPosition, lineNumber);
  }


  public static void main(String[] args) {
    //
    if(args.length == 0){
      System.out.println("usage: java lexer.Lexer filename.x");
      System.exit(1);
    }

    Token token;
    try {
      for (String arg : args) {
//        Lexer lex = new Lexer( "src\\sample_files\\simple.x" );

        Lexer lex = new Lexer(arg);
        while (true) {
          token = lex.nextToken();

          //p is formatted according to spec
//          System.out.println(token.toString());
        }
      }
    } catch (Exception e) {}
  }
}