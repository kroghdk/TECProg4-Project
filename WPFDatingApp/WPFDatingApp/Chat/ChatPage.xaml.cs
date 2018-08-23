using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Markup;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using WPFDatingApp;
using WPFDatingApp.ApiClasses;
using WPFDatingApp.Helpers;
using Page = System.Windows.Controls.Page;

namespace ChatBubbles
{
    /// <summary>
    /// Interaction logic for ChatPage.xaml
    /// </summary>
    public partial class ChatPage : Page
    {
        public MessageCollection messages = new MessageCollection();
        private Storyboard scrollViewerStoryboard;
        private DoubleAnimation scrollViewerScrollToEndAnim;

        private MessageSide curside;
        private string UserId { get; set; }
        private _mainFrame MainFrameRef { get; set; }

        private string LastLocalMessageId { get; set; }

        #region VerticalOffset DP

        /// <summary>
        /// VerticalOffset, a private DP used to animate the scrollviewer
        /// </summary>
        private DependencyProperty VerticalOffsetProperty { get; set; }


        private static void OnVerticalOffsetChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
        {
            ChatPage app = d as ChatPage;
            app.OnVerticalOffsetChanged(e);
        }

        private void OnVerticalOffsetChanged(DependencyPropertyChangedEventArgs e)
        {
            ConversationScrollViewer.ScrollToVerticalOffset((double)e.NewValue);
        }

        #endregion

        public ChatPage(string userid, _mainFrame mainFrameRef)
        {
            MainFrameRef = mainFrameRef;
            try
            {
                VerticalOffsetProperty = DependencyProperty.Register("VerticalOffset",
                typeof(double), typeof(ChatPage), new PropertyMetadata(0.0, OnVerticalOffsetChanged));
            }
            catch (Exception ex)
            {
                VerticalOffsetProperty = ConversationView.VerticalAlignmentProperty;
            }

            UserId = userid;

            InitializeComponent();

            var messagesFromPrevConvo = HttpClientHelper.Get<JsonLogin>($"GetMessage?USER={UserId}");

            if (messagesFromPrevConvo.Page?.Rows?.Row != null)
            {
                foreach (var message in messagesFromPrevConvo.Page.Rows.Row)
                {
                    if (message.MessageFromUser == UserId)
                    {
                        messages.Add(new Message()
                        {
                            Side = MessageSide.You,
                            Text = message.MessageContent,
                            Timestamp = (DateTime) new DateTimeConverter().ConvertFrom(message.MessageTimestamp)
                        });
                    }
                    else
                    {
                        messages.Add(new Message()
                        {
                            Side = MessageSide.Me,
                            Text = message.MessageContent,
                            Timestamp = (DateTime)new DateTimeConverter().ConvertFrom(message.MessageTimestamp)
                        });
                    }
                }

                LastLocalMessageId = messagesFromPrevConvo.Page.Rows.Row.LastOrDefault().MessageUserMesssageID;
            }

            
            this.DataContext = messages;

            scrollViewerScrollToEndAnim = new DoubleAnimation()
            {
                Duration = TimeSpan.FromSeconds(1),
                EasingFunction = new SineEase()
            };
            Storyboard.SetTarget(scrollViewerScrollToEndAnim, this);
            Storyboard.SetTargetProperty(scrollViewerScrollToEndAnim, new PropertyPath(VerticalOffsetProperty));

            scrollViewerStoryboard = new Storyboard();
            scrollViewerStoryboard.Children.Add(scrollViewerScrollToEndAnim);
            this.Resources.Add("foo", scrollViewerStoryboard);

            TextInput.Focus();

            GetNewMessages();
        }

        private void TextInput_GotFocus(object sender, RoutedEventArgs e)
        {
            ScrollConversationToEnd();
        }

        private void ScrollConversationToEnd()
        {
            scrollViewerScrollToEndAnim.From = ConversationScrollViewer.VerticalOffset;
            scrollViewerScrollToEndAnim.To = ConversationContentContainer.ActualHeight;
            //scrollViewerStoryboard.Begin(); TODO: fix
        }

        private void TextInput_LostFocus(object sender, RoutedEventArgs e)
        {
            ScrollConversationToEnd();
        }

        private void TextInput_PreviewKeyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Enter)
            {
                addTextMe(TextInput.Text, true);

                e.Handled = true;
            }
        }

        private void addTextMe(string text, bool sendToServer)
        {
            messages.Add(new Message()
            {
                Side = MessageSide.Me,
                Text = text,
                PrevSide = curside
            });

            curside = MessageSide.Me;

            ScrollConversationToEnd();

            TextInput.Text = "";
            TextInput.Focus();

            if (sendToServer)
            {
                // Send message to server
                var response = HttpClientHelper.Get<JsonLogin>($"SendMessage?MESSAGE_TO={UserId}&MESSAGE={text}");

                if (response.Page?.Rows?.Row != null)
                {
                    var lastMessage = response.Page.Rows.Row.FirstOrDefault()?.MessageUserMesssageID;

                    if (lastMessage != null)
                    {
                        // Set global prop
                        LastLocalMessageId = lastMessage;
                    }
                }
            }
        }

        private void addTextYou(string text)
        {
            messages.Add(new Message()
            {
                Side = MessageSide.You,
                Text = text,
                PrevSide = curside
            });

            curside = MessageSide.You;

            ScrollConversationToEnd();

            TextInput.Text = "";
            TextInput.Focus();
        }

        public Thread GetLatestMessagesThread { get; set; }
        private void GetNewMessages()
        {
            GetLatestMessagesThread = new Thread(StartThread) {IsBackground = true};
            GetLatestMessagesThread.Start();
        }

        private void StartThread()
        {
            while (true)
            {
                App.Current.Dispatcher.Invoke((Action) delegate {
                    if (!App.Current.MainWindow.Equals(MainFrameRef))
                    {
                        GetLatestMessagesThread.Abort();
                        return;
                    }
                });
                

                var newestMessages = HttpClientHelper.Get<JsonLogin>($"GetMessage?USER={UserId}&LAST_MESSAGE={LastLocalMessageId}");

                if (newestMessages.Page?.Rows?.Row != null)
                {
                    foreach (var message in newestMessages.Page.Rows.Row)
                    {
                        if (message.MessageFromUser == UserId)
                        {
                            App.Current.Dispatcher.Invoke((Action) delegate
                            {
                                messages.Add(new Message()
                                {
                                    Side = MessageSide.You,
                                    Text = message.MessageContent,
                                    Timestamp = (DateTime)new DateTimeConverter().ConvertFrom(message.MessageTimestamp)
                                });
                            });
                            
                        }
                        else
                        {
                            App.Current.Dispatcher.Invoke((Action)delegate
                            {
                                messages.Add(new Message()
                                {
                                    Side = MessageSide.Me,
                                    Text = message.MessageContent,
                                    Timestamp = (DateTime)new DateTimeConverter().ConvertFrom(message.MessageTimestamp)
                                });
                            });
                            
                        }
                    }

                    LastLocalMessageId = newestMessages.Page.Rows.Row.LastOrDefault().MessageUserMesssageID;
                }

                Thread.Sleep(500);
            }
        }

        private void Page_Unloaded(object sender, RoutedEventArgs e)
        {
            GetLatestMessagesThread.Abort();
        }
    }
}
