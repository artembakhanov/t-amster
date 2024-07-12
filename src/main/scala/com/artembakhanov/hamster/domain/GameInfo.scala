package com.artembakhanov.hamster.domain

import zio.json.JsonDecoder
import zio.json.JsonEncoder
import java.time.Instant

final case class GameInfo(count: Long, remaining: Long, lastUpdatedRemaining: Instant, allTimeMax: Long) derives JsonDecoder, JsonEncoder

object GameInfo:
  val empty = GameInfo(count = 0, remaining = 3000, lastUpdatedRemaining = Instant.MIN, allTimeMax = 0)


  case class Improvement(baseCost: Long, rate: Double, basePercentReward: Double, scalingFactor: Double):
    val levelsCost: Map[Int, Long] = 
      (0 to 14).map(n => n -> (baseCost * Math.pow(1 + rate, n)).ceil.toLong).toMap

    val rewards: Map[Int, Long] = 
      (1 to 15).map(n => n -> (levelsCost(n - 1) * basePercentReward * Math.pow(n, scalingFactor)).floor.toLong).toMap + (0 -> 0)

  
  val improvement1 = Improvement(200, 0.15, 0.005, -0.03)
  val improvement2 = Improvement(2000, 0.2, 0.0054, -0.01)


  println(improvement1.levelsCost.toList.sortBy(_._1))
  println(improvement1.rewards.toList.sortBy(_._1))

  println(improvement2.levelsCost.toList.sortBy(_._1))
  println(improvement2.rewards.toList.sortBy(_._1))


  // val miningLevels = Map[]
