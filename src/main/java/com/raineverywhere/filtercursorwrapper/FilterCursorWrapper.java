package com.raineverywhere.filtercursorwrapper;

import android.database.Cursor;
import android.database.CursorWrapper;

public class FilterCursorWrapper extends CursorWrapper {

    private int[] index;
    private int count=0;
    private int pos=0;

    private void normalIndex() {
        for (int i=0;i<this.count;i++) {
            this.index[i] = i;
        }
    }

    public FilterCursorWrapper(Cursor cursor, CallableWithInput<Cursor, Boolean> compareFn) {
        super(cursor);

        this.count = super.getCount();
        this.index = new int[this.count];

        for (int i=0;i<this.count;i++) {

            super.moveToPosition(i);

            if ( !compareFn.call(this) ) {
                this.index[this.pos++] = i;

            }

        }

        this.count = this.pos;
        this.pos = 0;
        super.moveToFirst();

    }

    public FilterCursorWrapper(Cursor cursor,String filter,int column, boolean invert) {
        super(cursor);

        this.count = super.getCount();
        this.index = new int[this.count];

        if (filter != null) {

            // filter by value
            for (int i=0;i<this.count;i++) {

                super.moveToPosition(i);

                String value = this.getString(column);
                if(value == null || value.length() == 0) break;

                if ( (value.toLowerCase().contains(filter.toLowerCase()) && !invert)
                        || (!value.toLowerCase().contains(filter.toLowerCase()) && invert)) {

                    this.index[this.pos++] = i;

                }

            }

            this.count = this.pos;
            this.pos = 0;
            super.moveToFirst();

        } else {

            normalIndex();

        }
    }

    @Override
    public boolean move(int offset) {
        return this.moveToPosition(this.pos+offset);
    }

    @Override
    public boolean moveToNext() {
        return this.moveToPosition(this.pos+1);
    }

    @Override
    public boolean moveToPrevious() {
        return this.moveToPosition(this.pos-1);
    }

    @Override
    public boolean moveToFirst() {
        return this.moveToPosition(0);
    }

    @Override
    public boolean moveToLast() {
        return this.moveToPosition(this.count-1);
    }

    @Override
    public boolean moveToPosition(int position) {
        return !(position >= this.count || position < 0) && super.moveToPosition(this.index[position]);
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public int getPosition() {
        return this.pos;
    }


    public interface CallableWithInput<I, O>  {

        public O call(I input);

    }
}
