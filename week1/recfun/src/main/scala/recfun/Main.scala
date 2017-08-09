package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
      print(pascal(col, row) + " ")
      println()
    }
  }
  /**
   * Exercise 1
   */
   def pascal(c: Int, r: Int): Int = if (c==0 || c==r) 1 else { pascal(c-1,r-1)+ pascal(c,r-1)  }
  /**
   * Exercise 2
   */
   def balance(charList: List[Char]): Boolean ={
        //val charList: List[Char] = chars.toList
        def acc_balance(remain_List: List[Char], acc:Int):Boolean ={
          if (remain_List.isEmpty) {
            acc==0
            }else if ( remain_List.head == '(' ) {
              acc_balance(remain_List.tail, acc+1)
              }else if (remain_List.head == ')' ) {acc>0 && acc_balance(remain_List.tail, acc-1)}
            else {acc_balance(remain_List.tail, acc)}

          }
          acc_balance(charList, acc=0)
        }

  /**
   * Exercise 3
   */
   def countChange(money: Int, coins: List[Int]): Int = 
   if (money ==0){
    1
    } else if (money > 0 && !coins.isEmpty ){
      countChange(money-coins.head, coins) + countChange(money, coins.tail ) ; 
      } else{
        0
      }
    }
