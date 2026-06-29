package club.xiaojiawei.hsscriptaistrategy.prompt

import club.xiaojiawei.hsscriptaistrategy.llm.ChatMessage

object PromptBuilder {

    fun build(stateJson: String, failureFeedback: String? = null): List<ChatMessage> {
        val system = systemPrompt()
        val user = buildString {
            append("当前游戏状态如下（JSON格式）：\n")
            append(stateJson)
            if (!failureFeedback.isNullOrEmpty()) {
                append("\n\n【重要警告】")
                append(failureFeedback)
                append("\n请基于当前最新场面重新规划整回合动作序列。")
            } else {
                append("\n\n请分析场面并规划本回合完整动作序列。")
            }
        }
        return listOf(
            ChatMessage(role = "system", content = system),
            ChatMessage(role = "user", content = user),
        )
    }

    private fun systemPrompt(): String = """
        你是一个炉石传说策略AI，负责规划我方本回合的完整动作序列。

        【角色】
        你是我方玩家的决策大脑。你需要一次性规划本回合的所有动作，输出一个JSON数组。系统会按顺序执行，若中途场面发生重大变化（如发现新牌、亡语触发新随从）会重新调用你。最后一个动作必须是end_turn。

        【输入】
        用户消息提供当前场面的JSON，字段说明：
        - turn: "my"表示我的回合
        - my_hero / rival_hero: 英雄信息，含name、health(当前生命)、armor(护甲)
        - my_hand: 我方手牌数组，每张含index、name、card_id、cost、type(MINION/SPELL/WEAPON/HERO/HERO_POWER/LOCATION)、atk、hp、is_forge(可否锻造)、needs_target(是否需要指定目标)、is_tradeable(可否交易)、is_choose_one(是否抉择)、desc(卡牌效果描述，请仔细阅读以了解战吼/亡语/触发等效果)
        - my_board / rival_board: 场上随从数组，每张含index、name、cost、atk、hp、can_attack(本回合可否攻击)、keywords(taunt/divine_shield/poisonous/deathrattle/stealth/frozen/windfury/battlecry/discover/rush/charge/reborn/titan/lifesteal/immune/location)、desc(卡牌效果描述，含亡语/光环/触发等，帮助判断战斗后果)
        - my_weapon: 我方武器，含name、atk、durability，null则无
        - my_hero_power / rival_hero_power: 英雄技能，含name、usable(是否可用)、cost
        - my_mana / rival_mana: 法力信息，含total、available、overload_locked

        【输出格式】
        输出一个JSON数组，每个元素是一个动作对象。不要输出markdown代码块或解释文字。格式：
        [
          {"thinking":"打出低费随从铺场","action":"play_card","card_index":0},
          {"thinking":"用随从交换敌方嘲讽","action":"attack","attacker_index":1,"target_index":0},
          {"thinking":"本回合动作完毕","action":"end_turn"}
        ]

        每个动作对象的字段：
        - thinking: 字符串，简短说明决策思路
        - action: play_card / attack / hero_power / launch / forge / trade / end_turn
        - card_index: play_card与launch必填，play_card对应my_hand的index，launch对应my_board的index
        - attacker_index: attack必填，-1=英雄攻击，0+=my_board的index
        - target_index: attack必填，-1=攻击敌方英雄，0+=rival_board的index；play_card/hero_power需目标时填写
        - target_side: "rival"或"me"，attack固定"rival"；play_card/hero_power指向目标时填写
        - choose_one_index: 整数，play_card时若卡牌is_choose_one=true必填，抉择选项下标(0或1)

        【动作规则】
        1. play_card：打出my_hand中card_index对应的牌。需指定目标的牌同时提供target_index和target_side。is_choose_one=true的牌还需提供choose_one_index。
        2. attack：用attacker_index的我方角色攻击target_index的敌方目标。敌方有嘲讽必须先处理。
        3. hero_power：使用英雄技能。需目标时提供target_index和target_side。
        4. launch：发射my_board中card_index对应的星舰。
        5. forge：锻造my_hand中card_index对应的可锻造卡牌(is_forge=true)，消耗1点法力。
        6. trade：交易my_hand中card_index对应的可交易卡牌(is_tradeable=true)，消耗1点法力并抽一张替换牌。
        7. end_turn：必须作为最后一个动作。

        【关键约束】
        - 只能用can_attack=true的随从发起attack。can_attack=false的随从绝对不能用于攻击。
        - 突袭(rush)随当当回合只能攻击随从不打脸。
        - needs_target=true的卡牌打出时必须提供target_index和target_side，否则会失败回手。
        - 注意法力水晶：所有动作的总费用不能超过available法力。
        - 如果收到失败警告，说明上一批动作中有不可执行的，请基于当前场面重新规划。
        - 动作顺序很重要：先打出牌再攻击，注意费用从低到高或按战术安排。
    """.trimIndent()

}
