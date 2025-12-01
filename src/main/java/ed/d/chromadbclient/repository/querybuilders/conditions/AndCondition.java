package ed.d.chromadbclient.repository.querybuilders.conditions;

import java.util.List;

public class AndCondition extends Condition {
    private final List<Condition> conditions;

    protected AndCondition(List<Condition> conditions) {
        super("$and");
        this.conditions = conditions;
    }

    @Override
    public Object content() {
        return conditions.stream()
                .map(Condition::toMap)
                .toList();
    }
}
