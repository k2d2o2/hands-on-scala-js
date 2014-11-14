import acyclic.file
import org.scalajs.dom

case class Tree[T](value: T, children: Vector[Tree[T]])

case class MenuNode(frag: dom.HTMLElement, start: Int, end: Int)

/**
 * High performance scrollspy to
 */
class ScrollSpy(headers: Vector[Double], domTrees: Seq[Tree[MenuNode]]){
  var scrolling = false
  var lastSelected: dom.HTMLElement = null
  def apply(threshold: Double) = if (!scrolling){
    scrolling = true
    dom.requestAnimationFrame((d: Double) => start(threshold))
  }
  def start(threshold: Double) = {
    scrolling = false
    def walkTree(tree: Tree[MenuNode]): Boolean = {
      val Tree(MenuNode(menuItem, index, next), children) = tree
      val before = headers(index) < threshold
      val after = (next >= headers.length) || headers(next) > threshold
      val win = before && after
      if (win){
        menuItem.classList.remove("hide")
        var winFound = false

        for(c <- tree.children){
          val newWinFound = walkTree(c)
          if (!winFound) c.value.frag.classList.add("selected")
          else c.value.frag.classList.remove("selected")
          winFound = winFound | newWinFound
        }
        if (!winFound) {
          tree.children.foreach(_.value.frag.classList.remove("selected"))
          if (lastSelected != null)
            lastSelected.children(0).classList.remove("pure-menu-selected")
          menuItem.children(0).classList.add("pure-menu-selected")
          lastSelected = menuItem
        }
      }else{
        menuItem.classList.add("hide")
        menuItem.classList.remove("selected")
      }
      win
    }
    domTrees.map(walkTree)
  }
}