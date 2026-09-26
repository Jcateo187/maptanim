package com.maptanim.app.dss.evaluator

import com.maptanim.app.dss.model.DssCategory
import com.maptanim.app.dss.model.DssDecision
import com.maptanim.app.dss.model.DssDecisionType
import com.maptanim.app.dss.model.DssPriority

/**
 * Collects raw decision results from all evaluators, performs deduplication,
 * resolves contradictions between concurrent rules, and assigns consistent
 * priority ordering.
 */
class DssConflictAndPriorityResolver {

    fun resolveAndPrioritize(rawDecisions: List<DssDecision>): List<DssDecision> {
        val filtered = mutableListOf<DssDecision>()
        val seenSignatures = mutableSetOf<String>()

        // 1. Deduplication by plot + category + title signature
        rawDecisions.forEach { decision ->
            val sig = "${decision.plotId}_${decision.category.name}_${decision.actionTaskType?.name}_${decision.title.take(20)}"
            if (!seenSignatures.contains(sig)) {
                seenSignatures.add(sig)
                filtered.add(decision)
            }
        }

        // 2. Conflict resolution
        // e.g. If a plot is marked HARVEST_READINESS (overdue or ready), suppress heavy fertilizer tasks on that same plot
        val harvestPlots = filtered
            .filter { it.category == DssCategory.HARVEST_READINESS && it.plotId != null }
            .mapNotNull { it.plotId }
            .toSet()

        val conflictResolved = filtered.filterNot { decision ->
            // Drop routine fertilization if the crop is already in harvest window
            decision.plotId in harvestPlots &&
            decision.category == DssCategory.GROWTH_CARE &&
            decision.actionTaskType == com.maptanim.app.domain.model.TaskType.FERTILIZE
        }

        // 3. Priority Sorting
        // Sort order:
        // 1. Priority rank (CRITICAL = 1, HIGH = 2, MEDIUM = 3, LOW = 4, INFO = 5)
        // 2. DecisionType (ALERT first, then TASK, then RECOMMENDATION)
        // 3. Plot label
        return conflictResolved.sortedWith(
            compareBy(
                { it.priority.rank },
                { decisionTypeOrder(it.decisionType) },
                { it.plotLabel ?: "" }
            )
        )
    }

    private fun decisionTypeOrder(type: DssDecisionType): Int = when (type) {
        DssDecisionType.ALERT -> 1
        DssDecisionType.TASK -> 2
        DssDecisionType.RECOMMENDATION -> 3
        DssDecisionType.INSUFFICIENT_INFO -> 4
    }
}
