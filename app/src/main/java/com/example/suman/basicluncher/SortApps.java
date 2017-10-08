package com.example.suman.basicluncher;

/**
 * Created by suman on 10/5/2017.
 */

public class SortApps {

    public void exchangeSort(Pac[] pacs){
        int i, j;
        Pac temp;

        for ( i=0;  i<pacs.length - 1;  i++ ) {
            for ( j=i+1;  j<pacs.length; j++ ) {
                if ( pacs[ i ].label.compareToIgnoreCase( pacs[ j ].label ) > 0 ) {  // ascending sort
                    temp = pacs [ i ];
                    pacs [ i ] = pacs [ j ];    // swapping
                    pacs [ j ] = temp;
                }
            }
        }
    }
}
