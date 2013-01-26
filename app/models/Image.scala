package com.mobileomega.models

import org.joda.time.DateTime
import org.jboss.netty.buffer._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._
import play.modules.reactivemongo.PlayBsonImplicits._

import reactivemongo.bson._
import reactivemongo.bson.handlers._



case class Image (
	id: Option[BSONObjectID],
	name: String,
	title: String,
	content: String,
	publisher: String,
	creationDate: Option[DateTime],
	updateDate: Option[DateTime],
	index: String
) 


object ImageHandlers {
	implicit object ImageBSONReader extends BSONReader[Image] {
		def fromBSON(document: BSONDocument): Image = {
			val doc = document.toTraversable
			Image(
				doc.getAs[BSONObjectID]("_id"),
				doc.getAs[BSONString]("name").get.value,
				doc.getAs[BSONString]("title").get.value,
				doc.getAs[BSONString]("content").get.value,
				doc.getAs[BSONString]("publisher").get.value,
				doc.getAs[BSONDateTime]("creationDate").map(dt => new DateTime(dt.value)),
				doc.getAs[BSONDateTime]("updateDate").map(dt => new DateTime(dt.value)),
				doc.getAs[BSONString]("index").get.value
			)
		}
	}

	implicit object ImageBSONWriter extends BSONWriter[Image] {
		def toBSON(image: Image) = {
			BSONDocument(
			"_id" -> image.id.getOrElse(BSONObjectID.generate),
			"name" -> BSONString(image.name),
			"title" -> BSONString(image.title),
			"content" -> BSONString(image.content),
			"publisher" -> BSONString(image.publisher),
			"creationDate" -> image.creationDate.map(date =>BSONDateTime(date.getMillis())),
			"updateDate" -> image.updateDate.map(date => BSONDateTime(date.getMillis())),
			"index" -> BSONString(image.index) 
			)
		}
	}


	val form = Form(
		mapping(
			"id" -> optional(of[String] verifying pattern(
				"""[a-fA-F0-9]{24}""".r,
				"constraint.objectId",
				"error.objectId")),
			"name" -> nonEmptyText,
			"title" -> nonEmptyText,
			"content" -> text,
			"publisher" -> nonEmptyText,
			"creationDate" -> optional(of[Long]),
			"updateDate" -> optional(of[Long]),
			"index" -> text
		) { (id, name, title, content, publisher, creationDate, updateDate, index) => 
			Image(
				id.map(new BSONObjectID(_)),
				name,
				title,
				content,
				publisher,
				creationDate.map(new DateTime(_)),
				updateDate.map(new DateTime(_)),
			    index)
		} { image => 
			Some(
				( image.id.map(_.stringify),
					image.name,
					image.title,
					image.content,
					image.publisher,
					image.creationDate.map(_.getMillis),
					image.updateDate.map(_.getMillis),
					image.index
				))
		})

}