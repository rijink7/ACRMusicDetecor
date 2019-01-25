#import "ACRMusicDetector.h"
#import "ACRCloudRecognition.h"
#import "ACRCloudConfig.h"

@interface ACRMusicDetector ()

@end

@implementation ACRMusicDetector
{
    ACRCloudRecognition         *_client;
    ACRCloudConfig          *_config;
    NSTimeInterval          startTime;
    __block BOOL    _start;
    NSString* _callbkId;
}
- (void)coolMethod:(CDVInvokedUrlCommand*)command
{
    
    NSString* name = [[command arguments] objectAtIndex:0];
    NSString* msg = [NSString stringWithFormat: @"Hello, %@", name];
    
    CDVPluginResult* result = [CDVPluginResult
                               resultWithStatus:CDVCommandStatus_OK
                               messageAsString:msg];
    
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}
- (void)init:(CDVInvokedUrlCommand*)command
{
    
    _start = NO;
    
    _config = [[ACRCloudConfig alloc] init];
    
    _config.accessKey = @"878427a85c26a8e44b91f8eddb77de91";
    _config.accessSecret = @"vL3K5M2SvUUr0Bz3ZpMJsK8NJKLb9MECop6mmven";
    _config.host = @"identify-us-west-2.acrcloud.com";
    _config.protocol = @"https";
    
    //if you want to identify your offline db, set the recMode to "rec_mode_local"
    _config.recMode = rec_mode_remote;
    _config.audioType = @"recording";
    _config.requestTimeout = 10;
    _config.keepPlaying = 1;
    
    /* used for local model */
    if (_config.recMode == rec_mode_local || _config.recMode == rec_mode_both)
        _config.homedir = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"acrcloud_local_db"];
    
    __weak id weakSelf = self;
    _config.resultBlock = ^(NSString *result, ACRCloudResultType resType) {
        [weakSelf handleResult:result resultType:resType];
    };
    
    //if you want to get the result and fingerprint, uncoment this code, comment the code "resultBlock".
    //_config.resultFpBlock = ^(NSString *result, NSData* fingerprint) {
    //    [weakSelf handleResultFp:result fingerprint:fingerprint];
    //};
    
    _client = [[ACRCloudRecognition alloc] initWithConfig:_config];
    
    //start pre-record
     [_client startPreRecord:4000];
    
    NSString* msg = [NSString stringWithFormat: @"init successfull"];
    
    CDVPluginResult* result = [CDVPluginResult
                               resultWithStatus:CDVCommandStatus_OK
                               messageAsString:msg];
    
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}


- (void)start:(CDVInvokedUrlCommand*)command
{
    _callbkId = command.callbackId;
    if (_start) {
        return;
    }
    
    [_client startRecordRec];
    _start = YES;
    
    startTime = [[NSDate date] timeIntervalSince1970];
}

- (void)startPreRec:(CDVInvokedUrlCommand*)command
{
    [_client startPreRecord:4000];
}



- (void)stop:(CDVInvokedUrlCommand*)command
{
    if(_client) {
        [_client stopRecordRec];
    }
    _start = NO;
}

- (void)cancel:(CDVInvokedUrlCommand*)command
{
    if(_client) {
        [_client stopRecordAndRec];
    }
    _start = NO;
}


- (void)stopPreRec:(CDVInvokedUrlCommand*)command
{
    if(_client) {
        [_client stopPreRecord];
    }
}

-(void)handleResult:(NSString *)result
         resultType:(ACRCloudResultType)resType
{
    
    
    dispatch_async(dispatch_get_main_queue(), ^{
        NSError *error = nil;
        
        NSData *jsonData = [result dataUsingEncoding:NSUTF8StringEncoding];
    
        NSDictionary* jsonObject = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:&error];
        
        CDVPluginResult* result = [CDVPluginResult
                                   resultWithStatus:CDVCommandStatus_OK
                                   messageAsDictionary:jsonObject];
        
        [self.commandDelegate sendPluginResult:result callbackId:_callbkId];
        
        NSLog(@"%@", result);
        
        
        [_client stopRecordRec];
        _start = NO;
        
        //        NSTimeInterval nowTime = [[NSDate date] timeIntervalSince1970];
        //        int cost = nowTime - startTime;
        //        self.costLabel.text = [NSString stringWithFormat:@"cost : %ds", cost];
        
    });
}

@end
