package controllers

import auth.AuthAction
import forms.BankForm.CreateBankForm
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, MessagesControllerComponents}
import repositories.BankRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BankController @Inject()(
                              cc: MessagesControllerComponents,
                              repo: BankRepository,
                              authAction: AuthAction
                              )(implicit ex : ExecutionContext) extends AbstractController(cc) {

  def addBank: Action[JsValue] = authAction.async(parse.json) { implicit request =>
    request.body.validate[CreateBankForm] match {
      case  JsSuccess(createBankForm, _) =>
        repo.addBank(createBankForm.toBankCustom)
      case JsError(errors) => println(errors)
    }
    Future.successful(Ok)
  }

  def updateBank(id: Long): Action[JsValue] = authAction.async(parse.json) { implicit r =>
    r.body.validate[CreateBankForm] match {
      case JsSuccess(data, _) =>
        repo.updateBank(id, data.name, data.bankCode, data.guichetCode, data.account, data.ribKey, data.iban, data.userId).map{ _ =>Ok}
      case JsError(errors) => Future.successful(BadRequest(Json.obj("status" -> "KO", "message" ->JsError.toJson(errors))))
    }
  }

  def deleteBank(id: Long): Action[AnyContent] = authAction.async { implicit r =>
    repo.deleteBank(id).map(_ => Redirect(routes.UserController.getUserList()))
  }

}
