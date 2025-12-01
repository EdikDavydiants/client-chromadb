package ed.d.chromadbclient.repository.querybuilders.conditions;

public class NotCondition extends Condition {
    private final Condition condition;

    protected NotCondition(Condition condition) {
        super("$not");
        this.condition = condition;
    }

    @Override
    public Object content() {
        return condition.toMap();
    }
}
