package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import scala.collection.mutable.ListBuffer

case class ShoppingCartItem(product: Product, var quantity: Int)

@Singleton
class CartController @Inject()(productsController: ProductController, val controllerComponents: ControllerComponents) extends BaseController {

  var products: ListBuffer[Product] = ListBuffer.from(productsController.getAllProducts())

  var shoppingCart: ListBuffer[ShoppingCartItem] = ListBuffer.empty[ShoppingCartItem]

  def showCart = Action { implicit request: Request[AnyContent] =>
    products = ListBuffer.from(productsController.getAllProducts())
    val totalPrice = shoppingCart.map(item => item.product.price * item.quantity).sum
    Ok(views.html.showCartView(shoppingCart.toSeq, totalPrice, products.toList))
  }


  def addToCart() = Action { implicit request: Request[AnyContent] =>
    val form = request.body.asFormUrlEncoded
    val productIdOpt = form.flatMap(_("productId").headOption.map(_.toLong))
    productIdOpt match {
      case Some(productId) =>
        products.find(_.id == productId) match {
          case Some(product) =>
            val existingItemOpt = shoppingCart.find(_.product.id == productId)
            existingItemOpt match {
              case Some(existingItem) =>
                existingItem.quantity += 1
              case None =>
                shoppingCart += ShoppingCartItem(product, 1)
            }
            Redirect(routes.CartController.showCart())
          case None =>
            BadRequest("Product not found")
        }
      case None =>
        BadRequest("Missing productId")
    }
  }


}
