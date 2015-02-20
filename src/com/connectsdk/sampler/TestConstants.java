package com.connectsdk.sampler;

public class TestConstants {

	
	public final static String Display_Image = "MediaPlayer.Display.Image";
	public final static String Play_Video = "MediaPlayer.Play.Video";
	public final static String Play_Audio = "MediaPlayer.Play.Audio";
	public final static String Close = "MediaPlayer.Close";
	
	public final static String MetaData_Title = "MediaPlayer.MetaData.Title";
	public final static String MetaData_Description = "MediaPlayer.MetaData.Description";
	public final static String MetaData_Thumbnail = "MediaPlayer.MetaData.Thumbnail";
	public final static String MetaData_MimeType = "MediaPlayer.MetaData.MimeType";
	
	public final static String MediaInfo_Get = "MediaPlayer.MediaInfo.Get";
	public final static String MediaInfo_Subscribe = "MediaPlayer.MediaInfo.Subscribe";
	
	
	public final static String[] MediaPlayerCapabilities = {
	    Display_Image,
	    Play_Video,
	    Play_Audio, 
	    Close,
	    MetaData_Title,
	    MetaData_Description,
	    MetaData_Thumbnail,
	    MetaData_MimeType,
	    MediaInfo_Get,
	    MediaInfo_Subscribe
	};
	
	public final static String Play = "MediaControl.Play";
	public final static String Pause = "MediaControl.Pause";
	public final static String Stop = "MediaControl.Stop";
	public final static String Rewind = "MediaControl.Rewind";
	public final static String FastForward = "MediaControl.FastForward";
    public final static String Seek = "MediaControl.Seek";
    public final static String Previous = "MediaControl.Previous";
    public final static String Next = "MediaControl.Next";
	public final static String Duration = "MediaControl.Duration";
	public final static String PlayState = "MediaControl.PlayState";
	public final static String PlayState_Subscribe = "MediaControl.PlayState.Subscribe";
	public final static String Position = "MediaControl.Position";
	
	public final static String[] MediaControlCapabilities = {
	    Play,
	    Pause,
	    Stop,
	    Rewind,
	    FastForward,
	    Seek,
        Previous,
        Next,
	    Duration,
	    PlayState,
	    PlayState_Subscribe,
	    Position,
	};
	
	 public final static String Application = "Launcher.App";
	    public final static String Application_Params = "Launcher.App.Params";
	    public final static String Application_Close = "Launcher.App.Close";
	    public final static String Application_List = "Launcher.App.List";
	    public final static String Browser = "Launcher.Browser";
	    public final static String Browser_Params = "Launcher.Browser.Params";
	    public final static String Hulu = "Launcher.Hulu";
	    public final static String Hulu_Params = "Launcher.Hulu.Params";
	    public final static String Netflix = "Launcher.Netflix";
	    public final static String Netflix_Params = "Launcher.Netflix.Params";
	    public final static String YouTube = "Launcher.YouTube";
	    public final static String YouTube_Params = "Launcher.YouTube.Params";
	    public final static String AppStore = "Launcher.AppStore";
	    public final static String AppStore_Params = "Launcher.AppStore.Params";
	    public final static String AppState = "Launcher.AppState";
	    public final static String AppState_Subscribe = "Launcher.AppState.Subscribe";
	    public final static String RunningApp = "Launcher.RunningApp";
	    public final static String RunningApp_Subscribe = "Launcher.RunningApp.Subscribe";
	    public final static String Amazon = "Launcher.Amazon";
	    public final static String Amazon_Params = "Launcher.Amazon.Params";

	    public final static String[] LauncherCapabilities = {
	        Application,
	        Application_Params,
	        Application_Close,
	        Application_List,
	        Browser,
	        Browser_Params,
	        Hulu,
	        Hulu_Params,
	        Netflix,
	        Netflix_Params,
	        YouTube,
	        YouTube_Params,
	        AppStore, 
	        AppStore_Params, 
	        AppState,
	        AppState_Subscribe,
	        RunningApp,
	        RunningApp_Subscribe
	    };
	    
	    public final static String JumpToTrack = "PlaylistControl.JumpToTrack";
	    public final static String SetPlayMode = "PlaylistControl.SetPlayMode";
	    public final static String PL_Previous = "PlaylistControl.Previous";
	    public final static String PL_Next = "PlaylistControl.Next";


	    public final static String[] PlayListControlCapabilities = {
	        PL_Previous,
	        PL_Next,
	        JumpToTrack,
	        SetPlayMode,
	        JumpToTrack,
	    };
	    public final static String Volume_Get = "VolumeControl.Get";
	    public final static String Volume_Set = "VolumeControl.Set";
	    public final static String Volume_Up_Down = "VolumeControl.UpDown";
	    public final static String Volume_Subscribe = "VolumeControl.Subscribe";
	    public final static String Mute_Get = "VolumeControl.Mute.Get";
	    public final static String Mute_Set = "VolumeControl.Mute.Set";
	    public final static String Mute_Subscribe = "VolumeControl.Mute.Subscribe";

	    public final static String[] VolumeControlCapabilities = {
	        Volume_Get,
	        Volume_Set,
	        Volume_Up_Down,
	        Volume_Subscribe,
	        Mute_Get,
	        Mute_Set,
	        Mute_Subscribe
	    };
	    
	    public final static String Channel_Get = "TVControl.Channel.Get";
	    public final static String Channel_Set = "TVControl.Channel.Set";
	    public final static String Channel_Up = "TVControl.Channel.Up";
	    public final static String Channel_Down = "TVControl.Channel.Down";
	    public final static String Channel_List = "TVControl.Channel.List";
	    public final static String Channel_Subscribe = "TVControl.Channel.Subscribe";
	    public final static String Program_Get = "TVControl.Program.Get";
	    public final static String Program_List = "TVControl.Program.List";
	    public final static String Program_Subscribe = "TVControl.Program.Subscribe";
	    public final static String Program_List_Subscribe = "TVControl.Program.List.Subscribe";
	    public final static String Get_3D = "TVControl.3D.Get";
	    public final static String Set_3D = "TVControl.3D.Set";
	    public final static String Subscribe_3D = "TVControl.3D.Subscribe";

	    public final static String[] TVControlCapabilities = {
	        Channel_Get,
	        Channel_Set,
	        Channel_Up,
	        Channel_Down,
	        Channel_List,
	        Channel_Subscribe,
	        Program_Get,
	        Program_List,
	        Program_Subscribe,
	        Program_List_Subscribe,
	        Get_3D,
	        Set_3D,
	        Subscribe_3D
	    };
	    
	    public final static String Picker_Launch = "ExternalInputControl.Picker.Launch";
	    public final static String Picker_Close = "ExternalInputControl.Picker.Close";
	    public final static String List = "ExternalInputControl.List";
	    public final static String Set = "ExternalInputControl.Set";

	    public final static String[] ExtInputControlCapabilities = {
	        Picker_Launch,
	        Picker_Close,
	        List,
	        Set
	    };
	    
	    public final static String Connect = "MouseControl.Connect";
	    public final static String Disconnect = "MouseControl.Disconnect";
	    public final static String Click = "MouseControl.Click";
	    public final static String Move = "MouseControl.Move";
	    public final static String Scroll = "MouseControl.Scroll";

	    public final static String[] MouseControlCapabilities = {
	        Connect,
	        Disconnect,
	        Click,
	        Move,
	        Scroll
	    };
	    
	    public final static String Send = "TextInputControl.Send";
	    public final static String Send_Enter = "TextInputControl.Enter";
	    public final static String Send_Delete = "TextInputControl.Delete";
	    public final static String Subscribe = "TextInputControl.Subscribe";

	    public final static String[] TextInputControlCapabilities = {
	        Send,
	        Send_Enter,
	        Send_Delete,
	        Subscribe
	    };
	    
	    public final static String Off = "PowerControl.Off";
	    public final static String On = "PowerControl.On";

	    public final static String[] PowerControlCapabilities = {
	        Off,
	        On
	    };
	    
	    public final static String Up = "KeyControl.Up";
	    public final static String Down = "KeyControl.Down";
	    public final static String Left = "KeyControl.Left";
	    public final static String Right = "KeyControl.Right";
	    public final static String OK = "KeyControl.OK";
	    public final static String Back = "KeyControl.Back";
	    public final static String Home = "KeyControl.Home";
	    public final static String Send_Key = "KeyControl.SendKey";

	    public final static String[] KeyControlCapabilities = {
	        Up,
	        Down,
	        Left,
	        Right,
	        OK,
	        Back,
	        Home
	    };
	    
	    public final static String Show_Toast = "ToastControl.Show";
	    public final static String Show_Clickable_Toast_App = "ToastControl.Show.Clickable.App";
	    public final static String Show_Clickable_Toast_App_Params = "ToastControl.Show.Clickable.App.Params";
	    public final static String Show_Clickable_Toast_URL = "ToastControl.Show.Clickable.URL";

	    public final static String[] ToastControlCapabilities = {
	        Show_Toast,
	        Show_Clickable_Toast_App,
	        Show_Clickable_Toast_App_Params,
	        Show_Clickable_Toast_URL
	    };
	    
	    public final static String Launch = "WebAppLauncher.Launch";
	    public final static String Launch_Params = "WebAppLauncher.Launch.Params";
	    public final static String Message_Send = "WebAppLauncher.Message.Send";
	    public final static String Message_Receive = "WebAppLauncher.Message.Receive";
	    public final static String Message_Send_JSON = "WebAppLauncher.Message.Send.JSON";
	    public final static String Message_Receive_JSON = "WebAppLauncher.Message.Receive.JSON";
	    public final static String WebApp_Connect = "WebAppLauncher.Connect";
	    public final static String WebApp_Disconnect = "WebAppLauncher.Disconnect";
	    public final static String Join = "WebAppLauncher.Join";
	    public final static String WebApp_Close = "WebAppLauncher.Close";
	    public final static String Pin = "WebAppLauncher.Pin";

	    public final static String[] WebAppLauncherCapabilities = {
	        Launch,
	        Launch_Params,
	        Message_Send,
	        Message_Receive,
	        Message_Send_JSON,
	        Message_Receive_JSON,
	        WebApp_Connect,
	        WebApp_Disconnect,
	        Join,
	        WebApp_Close,
	        Pin
	    };


}
