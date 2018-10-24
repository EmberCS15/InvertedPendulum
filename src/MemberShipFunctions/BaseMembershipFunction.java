package MemberShipFunctions;

public class BaseMembershipFunction {
    private String membershipId;

    public BaseMembershipFunction(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getMembershipId() {
        return membershipId;
    }
}
