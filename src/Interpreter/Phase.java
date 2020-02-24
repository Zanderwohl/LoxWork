package Interpreter;

public class Phase {

    private static String phaseName;

    /**
     * Called whenever a compiler phase is finished, to alert the user.
     * Maybe will have other things added to it later.
     * @param newPhase The name of the new phase. If there are no more phases, should be null.
     */
    public static void changePhase(String newPhase){
        if(phaseName != null){
            System.out.println(phaseName + " complete!");
        }
        if(newPhase != null) {
            phaseName = newPhase;
            System.out.println(phaseName + "...");
        }
    }
}
