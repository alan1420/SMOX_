<?php

namespace App\Http\Middleware;

use Illuminate\Auth\Middleware\Authenticate as Middleware;

class Authenticate extends Middleware
{
    /**
     * Get the path the user should be redirected to when they are not authenticated.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return string|null
     */
    protected function redirectTo($request)
    {
        if (! $request->expectsJson()) {
            //return route('login');
            //abort(403);
                    
        }
        abort(response()->json(['error' => '403'], 200));   
        //return response()->json([ 'error' => 404, 'message' => 'Not found' ], 404);
        // if ($request->expectsJson()) {
        //     return response()->json(['error' => 'Unauthenticated.'], 403);
        // }    
    }
}
