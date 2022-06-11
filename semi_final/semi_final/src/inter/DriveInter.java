/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inter;

/**
 *
 * @author jinah
 */
public class DriveInter implements InsertInter,SelectInter{

    @Override
    public void insertMethod() {
        System.out.println("insertMethod 실행");
    }

    @Override
    public void selectMethod() {
        System.out.println("selectMethod 실행");

    }
    
    
}
