package com.robut.markov.logging;

import java.util.Objects;

public class WordCombo {
    String pre;
    String post;

    public WordCombo(String newPre, String newPost){
        this.pre = newPre;

        this.post = newPost;
    }

    public String getPre() {
        return this.pre;
    }

    public String getPost() {
        return this.post;
    }

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }

        if (!(o instanceof WordCombo)){
            return false;
        }

        WordCombo words = (WordCombo) o;
//        if (    (this.getPre() == null && words.getPre() != null) ||
//                (this.getPre() != null && words.getPre() == null)
//                )
//        {
//            return false;
//        }
//        if (    (this.getPost() == null && words.getPost() != null) ||
//                (this.getPost() != null && words.getPost() == null)
//                )
//        {
//            return false;
//        }
//        if (this.getPre() == null && words.getPre() == null){
//
//        return  this.getPre().equals(words.getPre()) &&
//                this.getPost().equals(words.getPost());
        return Objects.equals(this.getPre(), words.getPre()) &&
                Objects.equals(this.getPost(), words.getPost());
    }

    @Override
    public int hashCode(){
        return Objects.hash(pre, post);
    }
}
