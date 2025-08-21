import jh61b.utils.Reflection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class ArrayDeque61BTest {

    @Test
    public void AddFirstTest() {
        ArrayDeque61B<Integer> Deck = new ArrayDeque61B<>();
        Deck.addFirst(1);
        Deck.addFirst(4);
        Deck.addFirst(9);
        assertThat(Deck.toList()).containsExactly(9, 4, 1).inOrder();
    }

    @Test
    public void AddLastTest() {
        ArrayDeque61B<Integer> Deck = new ArrayDeque61B<>();
        Deck.addLast(1);
        Deck.addLast(4);
        Deck.addLast(9);
        assertThat(Deck.toList()).containsExactly(1, 4, 9).inOrder();
    }

    @Test
    public void AddLastAndFirstTest() {
        ArrayDeque61B<Integer> Deck = new ArrayDeque61B<>();
        Deck.addLast(1);
        Deck.addLast(4);
        Deck.addLast(9);
        Deck.addFirst(5);
        Deck.addFirst(7);
        Deck.addFirst(8);
        assertThat(Deck.toList()).containsExactly(8, 7, 5, 1, 4, 9).inOrder();
    }

    @Test
    public void AddRemoveFirstTest() {
        ArrayDeque61B<Integer> Deck = new ArrayDeque61B<>();
        Deck.addLast(1);
        Deck.addLast(4);
        Deck.addLast(9);
        Deck.removeFirst();
        assertThat(Deck.toList()).containsExactly(4, 9).inOrder();
    }

    @Test
    public void AddRemoveLastTest() {
        ArrayDeque61B<Integer> Deck = new ArrayDeque61B<>();
        Deck.addLast(1);
        Deck.addLast(4);
        Deck.addLast(9);
        Deck.removeLast();
        assertThat(Deck.toList()).containsExactly(1, 4).inOrder();
    }

    @Test
    public void expandTest() {
        ArrayDeque61B<Integer> Deck = new ArrayDeque61B<>();
        for (int i = 0; i <= 99; i++) {
            Deck.addFirst(i);
        }
        assertThat(Deck.size()).isEqualTo(128);
    }

    @Test
    public void squeezeTest() {
        ArrayDeque61B<Integer> Deck = new ArrayDeque61B<>();
        for (int i = 0; i < 100; i++) {
            Deck.addFirst(i);
        }
        for (int i = 0; i < 69; i++) {
            Deck.removeFirst();
        }
        assertThat(Deck.size()).isEqualTo(64);
    }

    @Test
    public void squeezeLowerBoundTest() {
        ArrayDeque61B<Integer> Deck = new ArrayDeque61B<>();
        for (int i = 0; i < 100; i++) {
            Deck.addFirst(i);
        }
        for (int i = 0; i < 98; i++) {
            Deck.removeFirst();
        }
        assertThat(Deck.size()).isEqualTo(16);
    }

    @Test
    public void getTest() {
        ArrayDeque61B<Integer> Deck = new ArrayDeque61B<>();
        Deck.addLast(1);
        Deck.addLast(4);
        Deck.addLast(9);
        Deck.addFirst(5);
        Deck.addFirst(7);
        Deck.addFirst(8);
        assertThat(Deck.get(1)).isEqualTo(7);
    }


}
