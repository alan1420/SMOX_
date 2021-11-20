<?php

namespace App\Providers;

use App\Models\User;
use Illuminate\Foundation\Support\Providers\AuthServiceProvider as ServiceProvider;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Firebase\Auth\Token\Exception\InvalidToken;
use Illuminate\Support\Facades\Gate;

class AuthServiceProvider extends ServiceProvider
{
    /**
     * The policy mappings for the application.
     *
     * @var array
     */
    protected $policies = [
        // 'App\Models\Model' => 'App\Policies\ModelPolicy',
    ];

    /**
     * Register any authentication / authorization services.
     *
     * @return void
     */
    public function boot()
    {
        $this->registerPolicies();

        Auth::viaRequest('firebase', function (Request $request) {
            if ($request->bearerToken()) {
                $token = $request->bearerToken();
                $auth = app('firebase.auth');
    
                try {
                    $verifiedIdToken = $auth->verifyIdToken($token);
                    $uid = $verifiedIdToken->claims()->get('sub');
    
                    return User::find($uid);
                } catch (InvalidToken $e) {
                    echo 'The token is invalid: '.$e->getMessage();
                } catch (\InvalidArgumentException $e) {
                    echo 'The token could not be parsed: '.$e->getMessage();
                }
            }            
        });

        //
    }
}
