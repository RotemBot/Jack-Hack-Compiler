import scala.collection.mutable.ArrayBuffer

/**
  * Created by rotem on 20/03/2017.
  */
class Translator() {
  private final val ADD = ArrayBuffer(
    "@SP",
    "M=M-1",
    "A=M",
    "D=M",
    "A=A-1",
    "M=M+D"
  )
  private final val SUB = ArrayBuffer(
    "@SP",
    "M=M-1",
    "A=M",
    "D=M",
    "A=A-1",
    "M=M-D"
  )

  private final val NEG = ArrayBuffer(
    "@SP",
    "A=M",
    "A=A-1",
    "M=-M"
  )

  private final val NOT = ArrayBuffer(
    "@SP",
    "A=M",
    "A=A-1",
    "M=!M"
  )

  private final val AND = ArrayBuffer(
    "@SP",
    "A=M",
    "A=A-1",
    "D=M",
    "A=A-1",
    "M=M&D",
    "@SP",
    "M=M-1"
  )

  private final val OR = ArrayBuffer(
    "@SP",
    "A=M",
    "A=A-1",
    "D=M",
    "A=A-1",
    "M=M|D",
    "@SP",
    "M=M-1"
  )

  private final val EQ = ArrayBuffer(
    "@SP",
    "M=M-1",
    "A=M",
    "D=M",
    "A=A-1",
    "A=M",
    "D=A-D",
    "D;JEQ",
    "@SP",
    "A=M-1",
    "M=0",
    "0;JEQ",
    "@SP",
    "A=M-1",
    "M=-1"
  )

  private final val GT = ArrayBuffer(
    "@SP",
    "M=M-1",
    "A=M",
    "D=M",
    "A=A-1",
    "A=M",
    "D=A-D",
    "D;JGT",
    "@SP",
    "A=M-1",
    "M=0",
    "0;JEQ",
    "@SP",
    "A=M-1",
    "M=-1"
  )

  private final val LT = ArrayBuffer(
    "@SP",
    "M=M-1",
    "A=M",
    "D=M",
    "A=A-1",
    "A=M",
    "D=A-D",
    "D;JLT",
    "@SP",
    "A=M-1",
    "M=0",
    "0;JEQ",
    "@SP",
    "A=M-1",
    "M=-1"
  )

  //This is missing the first line, which defines the constant
  private final val PUSH_CONST = ArrayBuffer(
    "D=A",
    "@SP",
    "M=M+1",
    "A=M-1",
    "M=D"
  )

  //An aid. This number is used to name the TRUE and FALSE labels uniquely
  private var jumpCounter = 0

  /**
    * This method resets the label-name counter before reading a new file
    */
  def reset(): Unit= {
    jumpCounter = 0
  }


  def getAdd(): ArrayBuffer[String]= {
    ADD
  }

  def getSub(): ArrayBuffer[String]= {
    SUB
  }

  def getNeg(): ArrayBuffer[String]= {
    NEG
  }

  def getNot(): ArrayBuffer[String]= {
    NOT
  }

  def getAnd(): ArrayBuffer[String]= {
    AND
  }

  def getOr(): ArrayBuffer[String]= {
    OR
  }

  def getEq(): ArrayBuffer[String]= {
    countLables(EQ)
  }

  def getGt(): ArrayBuffer[String]= {
    countLables(GT)
  }

  def getLt(): ArrayBuffer[String]= {
    countLables(LT)
  }

  def getPushConst(const: String): ArrayBuffer[String]= {
    val copy = PUSH_CONST.clone()
    copy.insert(0, "@" + const)
    copy
  }

  /**
    * This method makes label names unique, in order to avoid making a mess...
    * @param array - The array of commands to edit
    * @return - An array of commands with uniquely named labels
    */
  private def countLables(array: ArrayBuffer[String]): ArrayBuffer[String]= {
    val copy = array.clone()
    copy.insert(7, "@TRUE" + jumpCounter)
    copy.insert(12, "@FALSE" + jumpCounter)
    copy.insert(14, "(TRUE" + jumpCounter + ")")
    copy.insert(18, "(FALSE" + jumpCounter + ")")
    jumpCounter += 1
    copy
  }


}
