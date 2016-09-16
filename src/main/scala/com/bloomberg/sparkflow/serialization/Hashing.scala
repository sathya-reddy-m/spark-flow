package com.bloomberg.sparkflow.serialization

import java.security.MessageDigest

import com.bloomberg.sparkflow.serialization.ClassExploration.{getClassReader, getClassesAndSerializedFields}
import com.google.common.io.BaseEncoding


/**
  * Created by ngoehausen on 3/23/16.
  */
private[sparkflow] object Hashing {

  def hashBytes(bytes: Array[Byte]) = {
    val md5 = MessageDigest.getInstance("MD5").digest(bytes)
    BaseEncoding.base16().encode(md5)
  }

  def hashString(s: String) = {
    hashBytes(s.getBytes())
  }

  def hashSeq[T](seq: Seq[T]) = {
    hashString(seq.mkString(";"))
  }

  def hashFunction(f: Any => Any) = {
    hashClass(f)
  }

  //TODO: fix hashing on case statement functions
  def hashClass(obj: AnyRef) = {

    val (allDepedendentClasses, serializedFields) = getClassesAndSerializedFields(obj)
    val combinedHashTarget = allDepedendentClasses
      .map(getClassReader)
      .map(_.b)
      .map(hashBytes) ++ serializedFields

    hashSeq(combinedHashTarget)

  }

}
