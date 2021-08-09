/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiapplication;

import java.io.Serializable;

/**
 *
 * @author ADMIN
 */
public class TablePrice implements Serializable, IObject{
    private Reporter reporter;
    private Post post;
    private int amount;
    
    

    public TablePrice() {
        
    }        
    
    public TablePrice(Reporter reporter, Post post) {
        this.reporter = reporter;
        this.post = post;
    }

    public TablePrice(Reporter reporter, Post post, int amount) {
        this.reporter = reporter;
        this.post = post;
        this.amount = amount;
    }

    public Reporter getReporter() {
        return reporter;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    @Override
    public Object[] toObject(){
        return new Object[]{String.valueOf(reporter.getId()), reporter.getName(), post.getNameClass(), String.valueOf(amount)};
    }
    
}
