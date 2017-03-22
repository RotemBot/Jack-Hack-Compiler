import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import util.control.Breaks._


/**
  * Created by rotem on 19/03/2017.
  */
object StackArithmeticConverter {

  // Contains the command translations
  val translator = new Translator

  def main(args: Array[String]): Unit = {
    val path = args(0)
    val fileNames = getListOfFiles(path)

    //iterate over all .vm files in directory
    for (file <- fileNames) {
      translator.reset()
      //create new file with suffix .asm
      val newFile = createAsmFile(file)

      //get lines to translate
      val filePath = Paths.get(file)
      val fileLines = Files.readAllLines(filePath)

      for (line <- fileLines.asScala.toList) {
        breakable {
          //Continue if the line is empty
          if (line.length <= 0) break

          //Continue if the line is a comment
          val firstTwoChars = line(0).toString + line(1).toString
          if (firstTwoChars.equals("//")) break

          //Send the line to be converted and written in .asm file
          convertLine(newFile, line)
        }

      }
    }

  }


  /**
    * returns the list of relevant files
    *
    * @param directoryName - The path to the directory
    * @return - A list of filename that end with .vm
    */
  def getListOfFiles(directoryName: String): Array[String] = {
    new File(directoryName).listFiles.
      filter { f => f.isFile && f.getName.endsWith(".vm") }.
      map(_.getAbsolutePath)
  }

  /**
    * Adds a line to the end of the file
    * @param fileName - The file to modify
    * @param line - The line to add
    */
  def writeLineToFile(fileName: String, line: String): Unit = {

    val file = Paths.get(fileName)
    val fileLines = Files.readAllLines(file)
    fileLines.add(line)
    Files.write(file, fileLines)
  }

  /**
    * Creates an empty file with .asm extension
    * @param oldFile - The path to the original file, the one we need to convert
    * @return - The path to the converted file (empty file)
    */
  def createAsmFile(oldFile: String): String = {
    val tokens = oldFile.split("""\.""") // Use a regex to avoid empty tokens
    val newFile = tokens(0) + ".asm"
    val path = Paths.get(newFile)
    // if the file already exists, overwrite it
    if(Files.exists(path)) {
      path.toFile.delete()
    }
    Files.createFile(path)
    newFile
  }

  /**
    * Gets a line, loads the matching translation and sends it to be written.
    * @param file - The file the translation is to be written in
    * @param line - The line to be translated
    */
  def convertLine(file: String, line: String): Unit = {
    val words = line.split(" ")
    words(0) match {
      case "add" => convert(file, translator.getAdd())

      case "sub" => convert(file, translator.getSub())

      case "push" => convert(file, translator.getPushConst(words(2)))

      case "neg" => convert(file, translator.getNeg())

      case "not" => convert(file, translator.getNot())

      case "eq" => convert(file, translator.getEq())

      case "gt" => convert(file, translator.getGt())

      case "lt" => convert(file, translator.getLt())

      case "and" => convert(file, translator.getAnd())

      case "or" => convert(file, translator.getOr())
    }
  }

  /**
    * Gets a file path and an array of strings, and writes them to the file line by line
    * @param fileToWrite - The file the lines are written to
    * @param commands - The array of strings, containing asm commands
    */
  def convert(fileToWrite: String, commands: ArrayBuffer[String]): Unit = {
    for(command <- commands) {
      writeLineToFile(fileToWrite, command)
    }
    //Add an empty line between translated commands to make the code more readable
    writeLineToFile(fileToWrite,"")

  }

}

