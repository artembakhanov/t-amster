package com.artembakhanov.hamster.domain

import zio.json.JsonDecoder
import zio.json.JsonEncoder
import java.time.Instant
import com.artembakhanov.hamster.domain.GameInfo.*
final case class GameInfo(
    count: Double,
    remaining: Long,
    lastUpdatedRemaining: Instant,
    allTimeMax: Double,
    level1: Int,
    level2: Int,
    level3: Int,
    level4: Int,
) derives JsonDecoder,
      JsonEncoder:
  def perSec: Double =
    improvement1.rewards(level1) +
    improvement2.rewards(level2) +
    improvement3.rewards(level3) +
    improvement4.rewards(level4)
end GameInfo

object GameInfo:
  val empty = GameInfo(
    count = 0,
    remaining = 3000,
    lastUpdatedRemaining = Instant.MIN,
    allTimeMax = 0,
    level1 = 0,
    level2 = 0,
    level3 = 0,
    level4 = 0,
  )

  case class Improvement(baseCost: Long, rate: Double, basePercentReward: Double, scalingFactor: Double):
    val levelsCost: Map[Int, Long] =
      (0 to 14).map(n => n -> (baseCost * Math.pow(1 + rate, n)).ceil.toLong).toMap

    val rewards: Map[Int, Double] =
      (1 to 15).map(n => n -> (levelsCost(n - 1) * basePercentReward * Math.pow(n, scalingFactor))).toMap + (0 -> 0)
  end Improvement

  val improvement1 = Improvement(200, 0.15, 0.005, -0.09)
  val improvement2 = Improvement(2000, 0.2, 0.0054, -0.04)
  val improvement3 = Improvement(100, 0.35, 0.007, -0.13)
  val improvement4 = Improvement(500, 0.08, 0.004, -0.02)
end GameInfo
