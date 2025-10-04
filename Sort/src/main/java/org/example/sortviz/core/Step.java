package org.example.sortviz.core;
public final class Step<T> {
    public final StepType type;
    public final int i; // primary index
    public final int j; // secondary index (for compare/swap)
    public final T value; // for SET
    public final String note;


    private Step(StepType type, int i, int j, T value, String note) {
        this.type = type; this.i = i; this.j = j; this.value = value; this.note = note;
    }
    public static <T> Step<T> compare(int i, int j, String note){return new Step<>(StepType.COMPARE,i,j,null,note);}
    public static <T> Step<T> swap(int i, int j, String note){return new Step<>(StepType.SWAP,i,j,null,note);}
    public static <T> Step<T> set(int i, T v, String note){return new Step<>(StepType.SET,i,-1,v,note);}
    public static <T> Step<T> markFinal(int i, String note){return new Step<>(StepType.MARK_FINAL,i,-1,null,note);}
    public static <T> Step<T> note(String msg){return new Step<>(StepType.NOTE,-1,-1,null,msg);}
}