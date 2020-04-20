package org.duder.viewModel.state;

public class FragmentState {
    private final Status status;
    private final Throwable error;

    public FragmentState(Status status) {
        this.status = status;
        error = null;
    }

    public FragmentState(Status status, Throwable error) {
        this.status = status;
        this.error = error;
    }

    public static FragmentState loading() {
        return new FragmentState(Status.LOADING);
    }

    public static FragmentState success() {
        return new FragmentState(Status.SUCCESS);
    }

    public static FragmentState complete() {
        return new FragmentState(Status.COMPLETE);
    }

    public static FragmentState error(Throwable error) {
        return new FragmentState(Status.ERROR, error);
    }

    public Status getStatus() {
        return status;
    }

    public Throwable getError() {
        return error;
    }
}
