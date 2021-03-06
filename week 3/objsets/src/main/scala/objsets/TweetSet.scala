package objsets

import TweetReader._

/**
 * A class to represent tweets.
 */
  class Tweet(val user: String, val text: String, val retweets: Int) {
    override def toString: String =
    "User: " + user + "\n" +
    "Text: " + text + " [" + retweets + "]"
    def >>(other:Tweet): Boolean = (retweets> other.retweets)
  }

  ////////////////////////////////////////////////
  abstract class TweetSet {
    def filter(p: Tweet => Boolean): TweetSet = filterAcc(p, new Empty)

    def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet


    def union(that: TweetSet): TweetSet = this.filterAcc( (t:Tweet) => !that.contains(t)  ,that)
    //def mostRetweeted: Tweet = if (descendingByRetweet.head == Nil) throw new java.util.NoSuchElementException("No such element exists") else descendingByRetweet.head
    def mostRetweeted: Tweet
    def descendingByRetweet: TweetList = {
      if (this.isInstanceOf[Empty]) Nil
      else{
        val m = mostRetweeted
        new Cons(m, this.remove(m).descendingByRetweet)
      }
    }
    

    def incl(tweet: Tweet): TweetSet
    def remove(tweet: Tweet): TweetSet

  /**
   * Tests if `tweet` exists in this `TweetSet`.
   */
   def contains(tweet: Tweet): Boolean

  /**
   * This method takes a function and applies it to every element in the set.
   */
   def foreach(f: Tweet => Unit): Unit
}



////////////////////////////////

class Empty extends TweetSet {
  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = acc

  /**
   * The following methods are already implemented
   */

   def contains(tweet: Tweet): Boolean = false

   def incl(tweet: Tweet): TweetSet = new NonEmpty(tweet, new Empty, new Empty)

   def remove(tweet: Tweet): TweetSet = this

   def foreach(f: Tweet => Unit): Unit = ()
   def mostRetweeted: Tweet = throw new java.util.NoSuchElementException("No such element exists")
}
/////////////////////////////////////////

class NonEmpty(elem: Tweet, left: TweetSet, right: TweetSet) extends TweetSet {

  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = {
    if ( p(elem) ) left.filterAcc(p, right.filterAcc(p, acc.incl(elem) ))
    else left.filterAcc(p, right.filterAcc(p, acc ))
  }


  /**
   * The following methods are already implemented
   */

   def contains(x: Tweet): Boolean =
   if (x.text < elem.text) left.contains(x)
   else if (elem.text < x.text) right.contains(x)
   else true

   def incl(x: Tweet): TweetSet = {
    if (x.text < elem.text) new NonEmpty(elem, left.incl(x), right)
    else if (elem.text < x.text) new NonEmpty(elem, left, right.incl(x))
    else this
   }

   def remove(tw: Tweet): TweetSet = {
    if (tw.text < elem.text) new NonEmpty(elem, left.remove(tw), right)
    else if (elem.text < tw.text) new NonEmpty(elem, left, right.remove(tw))
    else left.union(right)
   }

   def foreach(f: Tweet => Unit): Unit = {
    f(elem)
    left.foreach(f)
    right.foreach(f)
   }

   def mostRetweeted: Tweet = {
    def maxRetweet(s: Tweet, t: Tweet): Tweet = if (s>>t) s else t

    if (left.isInstanceOf[Empty]&& right.isInstanceOf[Empty] ) elem
    else if (left.isInstanceOf[Empty]) maxRetweet(elem,right.mostRetweeted)
    else if (right.isInstanceOf[Empty]) maxRetweet(elem,left.mostRetweeted)
    else maxRetweet(left.mostRetweeted, maxRetweet(elem,right.mostRetweeted))
   }


}
////////////////////////////////////////////////////////////////////////////
trait TweetList {
  def head: Tweet
  def tail: TweetList
  def isEmpty: Boolean
  def foreach(f: Tweet => Unit): Unit =
  if (!isEmpty) {
    f(head)
    tail.foreach(f)
  }
}

object Nil extends TweetList {
  def head = throw new java.util.NoSuchElementException("head of EmptyList")
  def tail = throw new java.util.NoSuchElementException("tail of EmptyList")
  def isEmpty = true
}

class Cons(val head: Tweet, val tail: TweetList) extends TweetList {
  def isEmpty = false
}


object GoogleVsApple {
  val google = List("android", "Android", "galaxy", "Galaxy", "nexus", "Nexus")
  val apple = List("ios", "iOS", "iphone", "iPhone", "ipad", "iPad")

  lazy val googleTweets: TweetSet = TweetReader.allTweets.filter(tweet=> google.exists(keyword => tweet.text.contains(keyword)))
  lazy val appleTweets: TweetSet = TweetReader.allTweets.filter(tweet=> apple.exists(keyword => tweet.text.contains(keyword)))

  /**
   * A list of all tweets mentioning a keyword from either apple or google,
   * sorted by the number of retweets.
   */
   lazy val trending: TweetList = googleTweets.union(appleTweets).descendingByRetweet
}

object Main extends App {
  // Print the trending tweets
  GoogleVsApple.trending foreach println

}