package club.xiaojiawei.data

import club.xiaojiawei.hsscriptbase.bean.CardWeight
import club.xiaojiawei.hsscriptbase.bean.LikeTrie

/**
 * @author 肖嘉威
 * @date 2024/11/14 9:02
 */

/**
 * 存储卡牌的权重
 * key: [club.xiaojiawei.bean.Card.cardId],value: [CardWeight]
 */
val CARD_WEIGHT_TRIE = LikeTrie<CardWeight>(CardWeight(1.0, 1.0, 0.0))