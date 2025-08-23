package deque;

import java.util.Comparator;
import java.util.Objects;

public class MaxArrayDeque61B <T> extends ArrayDeque61B<T>{
    Comparator<T> MaxArrayDequeComparator;
    public MaxArrayDeque61B(Comparator<T> c){
        super();
        MaxArrayDequeComparator=c;
    }

    public T max(){
        if(this.getUsed()==0)return null;
        T max=this.get(0);
        for(T x:this){
            if(MaxArrayDequeComparator.compare(x,max)>=0){
                max=x;
            }
        }
        return max;
    }

    public T max(Comparator<T> c){
        if(this.getUsed()==0)return null;
        T max=this.get(0);
        for(T x:this){
            if(c.compare(x,max)>=0){
                max=x;
            }
        }
        return max;
    }


}
