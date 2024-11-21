package club.xiaojiawei.data

import club.xiaojiawei.bean.CardWeight
import club.xiaojiawei.bean.LikeTrie

/**
 * @author 肖嘉威
 * @date 2024/11/14 9:02
 */

// 存储卡牌的权重
val CARD_WEIGHT_TRIE = LikeTrie<CardWeight>(CardWeight(1.0, 1.0))